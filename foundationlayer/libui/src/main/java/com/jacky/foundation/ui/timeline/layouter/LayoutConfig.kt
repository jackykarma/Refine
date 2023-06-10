package com.jacky.foundation.ui.timeline.layouter

import com.jacky.foundation.ui.timeline.TimeNodeType

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/9, jacky.li, Create file
 * @since 2023/6/9
 * @version v1.0.00
 */
data class LayoutConfig(
    val type: String = "INVALID",
    var columnOfBlock: Int = 0, // Block块的列数
    val rowOfBlock: Int = 0, // Block块的行数
    val pageCount: Int = 0, // 页数
    val nodeType: TimeNodeType = TimeNodeType.DAY, // 当前要显示的结点类型，默认是显示日视图
) {
    /**
     * 水平间距
     */
    var hGap: Int = 0

    /**
     * 垂直间距
     */
    var vGap: Int = 0

    /**
     * 每个插槽（ItemView)的半径（圆角）
     */
    var slotRadius: Float = 0f

    /**
     * 标题区域的高度
     */
    var titleRegionHeight: Int = 0

    /**
     * 每个TimeNode底部的边距（一个结点底部距离下一个结点的开头的距离）
     */
    var timeNodeBottomMargin: Int = 0

    /**
     * 水平内间距
     */
    var hPadding: Int = 0

    /**
     * 额外的信息
     */
    val extraInfo = mutableMapOf<String, Any>()

    /**
     * 最大的插槽数量
     */
    val maxSlotCount: Int
        get() = columnOfBlock * rowOfBlock

    fun isSingleColumn(): Boolean = columnOfBlock == 1

    companion object {
        /**
         * 屏幕的比例信息
         */
        const val EXTRA_SCREEN_RATIO = "screenRatio"

        /**
         * 插槽Slot的比例信息,ItemView的比例信息
         */
        const val EXTRA_SLOT_RATIO = "slotRatio"

        /**
         * 月视图的可绘制最大高度，是屏幕宽度减去左右padding，为了给2列时的单独成行的卡片变成占满整行使用
         */
        const val EXTRA_NODE_FULL_WIDTH = "nodeFullWidth"

        /**
         * 精选日视图，悬浮标题与toolbar工具栏之间的偏移量
         */
        const val EXTRA_FLOATING_TITLE_OFFSET = "floatingTitleOffset"
    }
}