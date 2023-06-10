package com.jacky.foundation.ui.timeline

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.core.view.ViewConfigurationCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.jacky.foundation.ui.R
import com.jacky.foundation.ui.timeline.fastscrollbar.FastScrollBar
import com.jacky.foundation.ui.timeline.gesture.TimelinePinchGestureDetector
import com.jacky.foundation.ui.timeline.layouter.BaseLayouter
import com.jacky.foundation.ui.timeline.layouter.OnLayoutListener
import com.jacky.foundation.ui.timeline.presentation.TimelineViewPresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description 复合列表视图
 * @author jacky.li
 * 2023/6/7, jacky.li, Create file
 * @since 2023/6/7
 * @version v1.0.00
 *
 * 许多设计思路是结构分解
 *
 * 复合列表视图
 *
 * TimelineView是整体的复合列表显示。
 * 它在显示上分为多种视图状态，比如年、月、日、缩放日、精选画廊、精选年、精选月、精选日等
 * 每个状态的显示用一个Presentation来显示，其中的显示布局利用Layouter布局器完成
 * 所有的Presentation的切换等操作由PresentationManager来负责管理。
 *
 * 而每个页面的数据加载、缓存等部分由SlidingWindow来负责。SlidingWindow由SlidingWindowManager管理。
 */
class TimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), OnLayoutListener {

    /**
     * 是否开启边界回弹效果
     */
    var overScrollEnable = true

    var downInScrolling = false

    /**
     * 布局反向
     */
    var revertLayoutDirection = true
        set(value) {
            if (field != value) {
                field = value
            }
        }

    /**
     * 滚动条的最小高度
     */
    private val scrollBarMinHeight by lazy { resources.getDimensionPixelSize(R.dimen.common_toolbar_height) }

    /**
     * 默认的边界回滚器
     */
    private val defaultOverScroller = OverScroller(context)

    /**
     * 边界回滚最大距离=屏幕的高度
     */
    private val overScrollMaxDistance: Int = context.resources.displayMetrics.heightPixels

    /**
     * 脱手滚动器
     */
    private val viewFlinger: ViewFlinger = ViewFlinger()

    /**
     * 最小的脱手速度
     */
    private val minFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity

    /**
     * 最大的脱手速速
     */
    private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

    /**
     * 滚动时溢出、晃荡的距离？
     */
    private val scrollSlop: Int =
        context.resources.getDimensionPixelSize(R.dimen.timeline_scroll_slop)

    /**
     * 垂直滚动的缩放因子？
     */
    private val scaledVerticalScrollFactor: Float by lazy {
        ViewConfigurationCompat.getScaledVerticalScrollFactor(
            ViewConfiguration.get(context),
            context
        )
    }

    /**
     * Presentation视图管理器
     */
    private val presentationManager: PresentationManager = PresentationManager(this, this)

    /**
     * Presentation视图的切换处理器
     */
    private val presentationSwitchProcessor: PresentationSwitchProcessor =
        PresentationSwitchProcessor(this, presentationManager)

    /**
     * 快速滑动条
     */
    var fastScrollBar: FastScrollBar? = null

    /**
     * 快滑条的最小高度
     */
    private val fastScrollBarMinHeight: Int by lazy { resources.getDimensionPixelSize(R.dimen.fast_scrollbar_min_height) }

    /**
     * 强制边界回滚。startScroll时，要执行computeScroll
     */
    private var forceOverScroll = false

    /**
     * 顶部真正的边界回滚距离
     */
    private var topRealOverScrollDistance = 0

    /**
     * 触摸事件启用开关
     */
    private var touchEventEnabled = true

    /**
     * 记录上一帧是否为边界回滚overScroll，用于computeScroll中辅助判断滑动停止
     */
    private var lastFrameIsOverScrolling = false

    /**
     * 边界回滚器
     */
    var overScroller: OverScroller = defaultOverScroller

    /**
     * 是否直接消费触摸事件
     */
    private var consumeTouchEventDirectly = false

    /**
     * 手势检测器
     */
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, GestureListener())
    }

    override fun onLayoutComplete() {
        TODO("Not yet implemented")
    }

    /**
     * 捏合手势的检测器
     */
    private val pinchGestureDetector: TimelinePinchGestureDetector by lazy {
        TimelinePinchGestureDetector(context, object : TimelinePinchGestureDetector.OnZoomListener {
            override fun onZoomBegin(pivotX: Int, pivotY: Int, isZoomInt: Boolean): Boolean {
                TODO("Not yet implemented")
            }

            override fun onZooming(distance: Float, diff: Float) {
                TODO("Not yet implemented")
            }

            override fun onZoomEnd(distance: Float, velocity: Float, zoomIn: Boolean) {
                TODO("Not yet implemented")
            }
        })
    }

    private var elementClickListener: OnElementClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    interface OnElementClickListener {
        fun onElementClick(
            nodeIndex: Int,
            itemIndex: Int,
            elementType: String,
            extra: Bundle = Bundle()
        )
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(itemIndex: Int)
    }

    private val hideFastScrollBarTask = Runnable {
        hideFastScrollBar()
    }

    private fun hideFastScrollBar() {
        fastScrollBar?.visibility = GONE
    }

    private var lifecycleScope: CoroutineScope? = null

    private var layoutScrollListener = OnScrollListener { notifyScrollChanged() }

    /**
     * 布局任务对象
     */
    private var layoutRunnable = Runnable {
        // 开始布局
    }

    /**
     * 是否启用布局
     */
    var isLayoutEnabled = true
        set(value) {
            // 跳过布局之后，重新启用，需要重新布局一次，确保布局正确
            if ((value != field) && value) {
                handler?.removeCallbacks(layoutRunnable)
                // 委托给显示管理器去执行onLayout布局操作
            }
            field = value
        }

    /**
     * 复写：是否允许垂直滚动
     */
    override fun canScrollVertically(direction: Int): Boolean {
        return when {
            // 上拉
            (direction > 0) && isInBottom().not() -> true
            (direction < 0) && isInTop().not() -> true
            else -> false
        }
    }

    private var touchHelper: TimelineAccessAbilityTouchHelper =
        TimelineAccessAbilityTouchHelper(this)

    fun init(
        lifecycleScope: LifecycleCoroutineScope,
        presentations: List<TimelineViewPresentation<*>>,
        defaultType: String? = null
    ) {
        this.lifecycleScope = lifecycleScope
        ViewCompat.setAccessibilityDelegate(this, touchHelper)
        presentationManager.setPresentations(presentations, defaultType)
        presentationManager.switchCallback = fun(oldType:String, newType:String) {
            // 要先切换SlidingWindow再回调callback
        }
    }

    private fun isInTop(): Boolean {
        return true
    }

    private fun isInBottom(): Boolean {
        return true
    }

    private fun notifyScrollChanged() {
        onScrollChanged(scrollX, scrollY, scrollX, scrollY)
    }

    inner class GestureListener : GestureDetector.OnGestureListener {

        /**
         * 滚动距离是否超过检查阈值Slop，当超过的一瞬间，将会检查是纵向滚动列表，还是横向切换ViewPager
         */
        private var isScrollExceedSlop = false

        /**
         * 是否处于横向滚动过程中
         */
        private var isHorizontalScrolling = false

        /**
         * 是否处于垂直（纵向）滚动过程中
         */
        private var isVerticalScrolling = false

        override fun onDown(e: MotionEvent): Boolean {
            TODO("Not yet implemented")
        }

        override fun onShowPress(e: MotionEvent) {
            TODO("Not yet implemented")
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // 滑动状态及OverScroll状态，点击不做响应
            return false
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onLongPress(e: MotionEvent) {
            TODO("Not yet implemented")
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    /**
     * 脱手滚动处理器，借鉴RecyclerView的ViewFlinger
     */
    inner class ViewFlinger : Runnable {

        override fun run() {
            TODO("Not yet implemented")
        }
    }
}