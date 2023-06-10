package com.jacky.foundation.ui.timeline.layouter

import android.graphics.Rect
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.math.MathUtils.clamp
import com.jacky.foundation.ui.timeline.decoration.BaseItemDecoration
import com.jacky.foundation.ui.timeline.DEFAULT_SELECT_MODE_SPEC
import com.jacky.foundation.ui.timeline.OnScrollListener
import com.jacky.foundation.ui.timeline.entity.TimeNode
import com.jacky.foundation.ui.timeline.TimelineView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/9, jacky.li, Create file
 * @since 2023/6/9
 * @version v1.0.00
 *
 * 时间轴复合列表的基础布局器
 */
abstract class BaseLayouter(
    /**
     * 布局器需要外部提供给它一个布局配置，按照布局配置要求来进行布局
     */
    layoutConfig: LayoutConfig,
    /**
     * 协程作用域由外部传入指定
     */
    private val coroutineScope: CoroutineScope,
    /**
     * 协程调度器由外部传入指定
     */
    private val dispatcher: CoroutineDispatcher
) {
    /**
     * 搞一个字段保存布局配置
     */
    var layoutConfig: LayoutConfig = layoutConfig

    /**
     * 锚点信息
     */
    var anchorItemInfo: Pair<Int, Int>? = null

    /**
     * 布局的视图的宽度
     */
    var viewWidth = 0
        protected set

    /**
     * 布局的视图的高度
     */
    var viewHeight = 0
        protected set

    /**
     * 顶部的内边距
     */
    var paddingTop = 0
        protected set

    /**
     * 底部的内边距
     */
    var paddingBottom = 0
        protected set

    /**
     * 布局的全局行区间
     * 意思：布局所要包括的行范围(确定要对哪些行进行布局)
     */
    var wholeRowRange = IntRange.EMPTY

    /**
     * 布局的全局Item区间
     * 意思：布局所要包括的Item范围(确定要对哪些Item进行布局)
     */
    var wholeItemRange = IntRange.EMPTY

    /**
     * 获取视图的中心Y坐标 (基于视图自身的坐标系)
     */
    val viewCenterYPosition: Int
        get() {
            // 如此计算的原因是顶部和底部的内边距不一样相同
            // 若相同，可以直接计算为 viewHeight / 2
            return (viewHeight - paddingBottom - paddingTop) / 2 + paddingTop
        }

    /**
     * 可见的结点范围(包含的结点范围)
     */
    protected var visibleNodeRange = IntRange.EMPTY

    /**
     * 可见的Block块范围（包含的Block范围）
     */
    protected var visibleBlockRange = IntRange.EMPTY

    /**
     * 可见的Item范围
     */
    protected var visibleItemRange = IntRange.EMPTY

    /**
     * 布局包含的所有时间节点
     */
    protected val timeNodes = CopyOnWriteArrayList<TimeNode>()

    /**
     * 节点的布局参数列表
     */
    protected val nodeLayoutParams = mutableListOf<NodeLayoutParam>()

    /**
     * 用于在异步线程存储布局信息，布局完成后会全部copy到相应的主线程对象
     */
    protected val nodeLayoutParamsAsync = mutableListOf<NodeLayoutParam>()

    /**
     * 布局参数的读写锁
     */
    protected val layoutParamsRWLock = ReentrantReadWriteLock()

    /**
     * 上次进行异步布局的任务对象（协程任务）
     */
    private var lastAsyncLayoutJob: Job? = null

    private val layoutLock = ReentrantLock(true)

    /**
     * 是否正在布局过程中
     */
    private var isLayouting = false

    /**
     * Item布局装饰
     */
    private val itemDecorations = object : LinkedHashMap<Int, BaseItemDecoration>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, BaseItemDecoration>?): Boolean {
            return size >= visibleItemRange.length() * ITEM_DECORATIONS_CACHE_TIMES
        }
    }

    /**
     * 搜索结果缓冲，用于保存上一次查询结果 FIXME:居然还耦合搜索业务
     * 当遍历查询连续同一Node中多行item所在节点时，只有第一个会进行全局查询，后面可以直接返回结果
     */
    protected var searchNodeIndex = 0

    /**
     * 布局可见区域变化的监听器
     */
    protected var visibleRangeChangeListener: OnLayoutVisibleChangeListener? = null

    protected var scrollListener: OnScrollListener? = null
    protected var layoutListener: OnLayoutListener? = null

    protected var layoutHeight = viewHeight
    protected var scrollPosition = 0

    /**
     * 布局后的滚动偏移量
     */
    protected var scrollOffsetAfterLayout = 0

    protected var footerHeight: Int = 0
    protected var headerHeight: Int = 0

    protected var scrollPositionForVisibleRange = -1

    var isRTLMode = false

    protected val tmpRect = Rect()

    protected var slotWidthWithGap = 0
    protected var slotHeightWithGap = 0

    protected var slotWidth = 0
        set(value) {
            field = value
            slotWidthWithGap = value + layoutConfig.hGap
        }

    protected var slotHeight = 0
        set(value) {
            field = value
            slotHeightWithGap = value + layoutConfig.vGap
        }

    /**
     * 布局可见区域变化的监听器
     */
    interface OnLayoutVisibleChangeListener {
        /**
         * 列表可见区域变化时触发，只能在主线程调用
         */
        @MainThread
        fun onLayoutVisibleChanged(
            type: String,
            nodeRange: IntRange,
            blockRange: IntRange,
            itemRange: IntRange,
            newLayout: Boolean
        )

        /**
         *  列表布局后预先在布局线程通知业务可见区域变化，增删动画流程依赖该接口
         */
        @MainThread
        fun onPreLayoutVisibleChanged(
            type: String,
            nodeRange: IntRange,
            blockRange: IntRange,
            itemRange: IntRange
        )
    }

    fun IntRange.length(): Int = if (isEmpty()) 0 else (last - first + 1)

    /**
     * 布局是否反向
     */
    open var revertLayoutDirection = true
        set(value) {
            if (field != value) {
                field = value
                layoutAsync(viewWidth, viewHeight, forceRefresh = true)
            }
        }

    /**
     * 喂数据
     * 设置时间节点数据
     */
    open fun setTimeNodes(timeNodes: List<TimeNode>) {
        synchronized(this.timeNodes) {
            this.timeNodes.clear()
            this.timeNodes.addAll(timeNodes)
        }
    }

    /**
     * 子线程布局
     */
    @MainThread
    open fun layoutAsync(width: Int, height: Int, forceRefresh: Boolean) {
        if ((width <= 0) || (height <= 0)) {
            return
        }
        val lastScrollLimit = scrollLimit()
        val heightChanged = viewHeight != height
        if (heightChanged) viewHeight = height

        if (!forceRefresh && (viewWidth == width) && !isLayouting) {
            // 宽度没变，高度变化了
            if (heightChanged) {
                if (revertLayoutDirection) {
                    scrollPosition += scrollLimit() - lastScrollLimit
                }
                clampScrollPosition()
                if (updateVisibleRange(force = true)) {
                    visibleRangeChangeListener?.onLayoutVisibleChanged(
                        layoutConfig.type,
                        visibleNodeRange,
                        visibleBlockRange,
                        visibleItemRange,
                        false
                    )
                }
            }
            return
        }

        lastAsyncLayoutJob?.cancel()
        lastAsyncLayoutJob = coroutineScope.launch(dispatcher) {
            layoutSyncInternal(width, height, preUpdateVisibleRange = false, scope = this)
        }
    }

    fun layoutSync(
        width: Int,
        height: Int,
        preUpdateVisibleRange: Boolean,
        scope: CoroutineScope? = null,
        anchorItemIndex: Int = -1
    ) {
        // 先将异步布局的任务取消，再进行同步布局方法
        lastAsyncLayoutJob?.cancel()
        layoutSyncInternal(width, height, preUpdateVisibleRange, scope, anchorItemIndex)
    }

    @Synchronized
    protected open fun layoutSyncInternal(
        width: Int,
        height: Int,
        preUpdateVisibleRange: Boolean,
        scope: CoroutineScope? = null,
        anchorItemIndex: Int = -1
    ) {
        if (width <= 0 || height <= 0) {
            return
        }
        // 标记正在布局中
        isLayouting = true
        try {
            layoutLock.lock()
            layoutInternal(width, scope)
            // 视图切换过程中，阻塞布局，避免内容跳变
        } catch (e: Exception) {
            Log.e(TAG, "layoutSyncInternal: ${e.message}")
        } finally {
            layoutLock.unlock()
        }

        if (preUpdateVisibleRange) {
            val lastScrollLimit = scrollLimit()
            val bottom =
                if (nodeLayoutParamsAsync.isNotEmpty()) nodeLayoutParamsAsync.first().bottom else 0
            val scrollLimit = (bottom + paddingBottom + footerHeight - viewHeight).coerceAtLeast(0)
            val scrollPos = if (revertLayoutDirection) {
                scrollPosition + scrollLimit - lastScrollLimit
            } else {
                clamp(scrollPosition, scrollStart(), scrollLimit)
            }
            if (updateVisibleRange(scrollPos, nodeLayoutParamsAsync, force = true)) {
                visibleRangeChangeListener?.onPreLayoutVisibleChanged(
                    layoutConfig.type,
                    visibleNodeRange,
                    visibleBlockRange,
                    visibleItemRange
                )
                scrollPositionForVisibleRange = Int.MAX_VALUE
            }
        }
    }

    abstract fun layoutInternal(width: Int, scope: CoroutineScope?)

    /**
     * 更新视图可见区域
     * @return 可见区域是否发生变化
     */
    abstract fun updateVisibleRange(
        scrollPosition: Int = this.scrollPosition,
        nodeParams: List<NodeLayoutParam> = this.nodeLayoutParams,
        force: Boolean = false
    ): Boolean

    fun setPadding(paddingTop: Int, paddingBottom: Int) {
        if ((this.paddingTop == paddingTop) && (this.paddingBottom == paddingBottom)) {
            return
        }
        val isInTop = isInTop()
        val isInBottom = isInBottom()
        this.paddingTop = paddingTop
        this.paddingBottom = paddingBottom
        when {
            isInTop -> adjustPosition(true, scrollStart())
            isInBottom -> adjustPosition(true, scrollLimit())
            else -> adjustPosition(false, 0)
        }
    }

    private fun isInBottom(): Boolean = scrollPosition == scrollLimit()

    private fun isInTop(): Boolean = scrollPosition == scrollStart()

    private fun adjustPosition(enable: Boolean, scrollStart: Int) {
        if (enable) scrollTo(scrollStart)
        else scrollBy(0)
    }

    private fun scrollBy(dy: Int, overScrollEnable: Boolean = false): Int {
        val newScrollPosition = if (overScrollEnable) {
            scrollPosition + dy
        } else {
            // 确保滚动的距离是在最小scrollStart和最大限制的距离范围内
            clamp(scrollPosition + dy, scrollStart(), scrollLimit())
        }
        // 需要滚动的距离
        val consumed = newScrollPosition - scrollPosition
        // 更新当前滚动位置
        scrollPosition = newScrollPosition
        if (consumed != 0) {
            if (updateVisibleRange()) {
                visibleRangeChangeListener?.onLayoutVisibleChanged(
                    layoutConfig.type,
                    visibleNodeRange,
                    visibleBlockRange,
                    visibleItemRange,
                    false
                )
            }
            scrollListener?.onScroll()
        }
        return consumed
    }

    private fun scrollTo(position: Int, overScrollEnable: Boolean = false) {
        // 当前的滚动位置已经是目标要滚动的位置，直接return
        if (position == scrollPosition) {
            return
        }
        scrollPosition = if (overScrollEnable) {
            position
        } else {
            clamp(position, scrollStart(), scrollLimit())
        }
        if (updateVisibleRange()) {
            visibleRangeChangeListener?.onLayoutVisibleChanged(
                layoutConfig.type,
                visibleNodeRange,
                visibleBlockRange,
                visibleItemRange,
                false
            )
        }
        // 触发滚动监听器
        scrollListener?.onScroll()
    }

    fun clampScrollPosition(): Int {
        val newScrollPostion = clamp(scrollPosition, scrollStart(), scrollLimit())
        val offset = newScrollPostion - scrollPosition
        scrollPosition = newScrollPostion
        return offset
    }

    /**
     * 处理点击事件
     * @param x View的屏幕相对坐标x
     * @param y View的屏幕相对坐标y
     * @param ignoreTitle true->x,y位置为title，则返回标题下方的Item
     * @param selectModeSpec 选择模式规则
     * @param alternateLast true->,y位置为Block末行空白区域时，返回该行最后Item的Index
     */
    abstract fun dispatchClickEvent(
        x: Int,
        y: Int,
        ignoreTitle: Boolean = false,
        alternateLast: Boolean = false,
        selectModeSpec: Int = DEFAULT_SELECT_MODE_SPEC,
        clickListener: TimelineView.OnElementClickListener
    )

    /**
     * 查找指定位置的可见Item
     * @param x View的屏幕相对坐标x
     * @param y View的屏幕相对坐标y
     * @param outRect 存放Item的矩形区域Rect
     * @param ignoreTitle true->x,y位置为title，则返回标题下方的Item
     * @param selectModeSpec 选择模式规则
     * @param alternateLast true->,y位置为Block末行空白区域时，返回该行最后Item的Index
     */
    abstract fun findVisibleItemUnder(
        x: Int,
        y: Int,
        outRect: Rect? = null,
        ignoreTitle: Boolean = false,
        alternateLast: Boolean = false,
        selectModeSpec: Int = DEFAULT_SELECT_MODE_SPEC,
    ): Pair<Int, Int>

    /**
     * 滚动的最大距离、滚动的限制距离
     * layoutHeight是布局的高度，viewHeight是view的高度
     */
    fun scrollLimit(): Int = (layoutHeight - viewHeight).coerceAtLeast(0) + scrollStart()

    private fun scrollStart(): Int = -(paddingTop + headerHeight)

    fun scrollPosition(): Int = scrollPosition

    fun setVisibleRangeChangeListener(listener: OnLayoutVisibleChangeListener) {
        visibleRangeChangeListener = listener
    }

    fun setOnLayoutListener(listener: OnLayoutListener) {
        layoutListener = listener
    }

    companion object {
        private const val TAG = "BaseLayouter"

        private const val ITEM_DECORATIONS_CACHE_TIMES = 2
    }
}