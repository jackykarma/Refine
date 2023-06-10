package com.jacky.foundation.ui.timeline.decoration

import android.graphics.Rect
import com.jacky.foundation.ui.timeline.layouter.LayoutConfig

/**
 * 单个节点装饰
 *
 * 整个标题区域
 */
open class BaseNodeDecoration(var index: Int) {
    /**
     * 布局配置
     */
    var config: LayoutConfig = LayoutConfig()

    /**
     * 整个节点区域的坐标
     */
    val nodeRect = Rect()

    /**
     * 主标题区域中，文字区域的坐标
     */
    val titleContentRect = Rect()

    /**
     * 副标题区域中，文字区域的坐标
     */
    val subTitleContentRect = Rect()

    /**
     * 是否脏了
     */
    var isDirty = true

    /**
     * 布局方向是否是RTL
     */
    var isRTL = false

    /**
     * 选中框的区域
     */
    val checkBoxRect = Rect()

    companion object {
        const val INVALID_DATE = 9999
    }
}
