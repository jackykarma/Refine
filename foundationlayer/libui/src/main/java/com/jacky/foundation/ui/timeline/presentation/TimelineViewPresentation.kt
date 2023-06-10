package com.jacky.foundation.ui.timeline.presentation

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import com.jacky.foundation.ui.timeline.layouter.BaseLayouter
import com.jacky.foundation.ui.timeline.layouter.LayoutConfig
import com.jacky.foundation.ui.timeline.layouter.OnLayoutListener
import com.jacky.foundation.ui.timeline.layouter.OnLayoutVisibleChangeListener
import com.jacky.foundation.ui.timeline.StateChangeObserver
import com.jacky.foundation.ui.timeline.entity.TimelineInfo
import com.jacky.foundation.ui.timeline.TimelineView

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/9, jacky.li, Create file
 * @since 2023/6/9
 * @version v1.0.00
 *
 * 时间轴复合视图，每种视图显示状态的抽象类 Presentation
 * 本质是一个视图对象.
 */
abstract class TimelineViewPresentation<T : BaseLayouter>(
    config: LayoutConfig = LayoutConfig()
) : BaseLayouter.OnLayoutVisibleChangeListener, OnLayoutListener {

    protected var width = 0
    private var height = 0

    /**
     * 上次更新标题的时间戳
     */
    private var lastTitleUpdateTime = System.currentTimeMillis()

    protected val stateChangeObservers = mutableListOf<StateChangeObserver>()
    protected val tmpRect: Rect = Rect()
    protected val tmpRectF: RectF = RectF()
    protected val toolbarRect: Rect = Rect()

    var isForeground = false
        private set

    var layoutConfig = config
        protected set

    var totalCount: Int = -1

    var layoutVisibleChangeListener: OnLayoutVisibleChangeListener? = null
    var layoutListener: OnLayoutListener? = null

    lateinit var timelineView: TimelineView
    lateinit var layouter: T
    lateinit var timelineInfo: TimelineInfo

    /**
     * 设置视图的占位Slot信息
     */
    var placeHolder: PlaceHolder? = null
        set(value) {
            if (field != value) {
                field = value
                // 占位符变更
                onPlaceHolderChanged(field)
                // 时间轴整个视图都要刷新
                timelineView.invalidate()
            }
        }

    open fun onPlaceHolderChanged(placeHolder: PlaceHolder?) {
        // do nothing
    }

    open var revertLayoutDirection:Boolean = true
        set(value) {
            if (field != value) {
                field = value
                layouter.revertLayoutDirection = value
            }
        }

    open fun updateLayoutConfig(config: LayoutConfig) {
        layoutConfig = config
        layouter.layoutConfig = config
    }

    fun init(view: TimelineView) {
        timelineView = view
        layouter = onCreateLayouter()
        layouter.setVisibleRangeChangeListener(this)
        layouter.setOnLayoutListener(this)
        layouter.revertLayoutDirection = revertLayoutDirection
        onInit()
    }

    /**
     * 视图初始化
     */
    abstract fun onInit()

    /**
     * 如何布局决定如何显示，当然是由显示presentation的子类去创建的
     */
    abstract fun onCreateLayouter(): T

    /**
     * 视图内容绘制, 子类布局、子类绘制
     */
    abstract fun onDraw(canvas: Canvas)

    /**
     * 视图切换时的视图内容绘制
     */
    abstract fun onDrawWhenSwitching(canvas: Canvas)

}
