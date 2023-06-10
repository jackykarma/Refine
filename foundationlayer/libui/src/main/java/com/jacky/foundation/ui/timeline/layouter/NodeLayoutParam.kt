package com.jacky.foundation.ui.timeline.layouter

import com.jacky.foundation.ui.timeline.decoration.BaseNodeDecoration

/**
 * 用于收集、保存配置时间节点布局的参数
 */
class NodeLayoutParam {

    lateinit var nodeDecoration: BaseNodeDecoration

    var index = 0
        set(value) {
            field = value
            nodeDecoration.index = index
        }

    /**
     * 划分多个block好似是为了提高加载效率
     *
     *   --------------- top
     *   |    Title     |
     *   --------------- centerTop
     *   |     Block    |
     *   ----------------
     *   |     Block    |
     *   ----------------
     *   |     ...      |
     *   ---------------- contentBottom
     *   |     Margin   |
     *   ---------------- bottom
     */
    var left = 0
    var top = 0
    var centerTop = 0
    var right = 0
    var contentBottom = 0
    var bottom = 0

    /**
     * 该节点第一个Item的索引号
     */
    var firstItemIndex = 0
        set(value) {
            field = value
            lastItemIndex = value
        }

    /**
     * 最后一个Item的索引号
     */
    var lastItemIndex = 0
        private set

    /**
     * Item的数量
     */
    var itemCount = 0
        set(value) {
            field = value
            lastItemIndex = firstItemIndex + value - 1
        }

    /**
     * 第一个block的索引
     */
    var firstBlockIndex = 0
        set(value) {
            field = value
            lastBlockIndex = value
        }

    /**
     * 最后一个Block块的索引
     */
    var lastBlockIndex = 0
        private set

    /**
     * block块的数量
     */
    var blockCount = 0
        set(value) {
            field = value
            lastBlockIndex = firstBlockIndex + value - 1
        }

    /**
     * 第一行的索引
     */
    var fistRowIndex = 0
        set(value) {
            field = value
            lastRowIndex = value
        }

    /**
     * 最后一行的索引
     */
    var lastRowIndex = 0
        private set

    /**
     * 行数
     */
    var rowCount = 0
        set(value) {
            field = value
            lastRowIndex = firstBlockIndex + value -1
        }

    /**
     * 节点的宽度
     */
    fun width() = right - left

    /**
     * 节点的高度
     */
    fun height() = bottom - top

    override fun toString(): String {
        return "NodeLayoutParam(nodeDecoration=$nodeDecoration, " +
                "index=$index, left=$left, top=$top, centerTop=$centerTop, " +
                "right=$right, contentBottom=$contentBottom, bottom=$bottom, " +
                "firstItemIndex=$firstItemIndex, lastItemIndex=$lastItemIndex, " +
                "itemCount=$itemCount, firstBlockIndex=$firstBlockIndex, " +
                "lastBlockIndex=$lastBlockIndex, blockCount=$blockCount, " +
                "fistRowIndex=$fistRowIndex, lastRowIndex=$lastRowIndex, rowCount=$rowCount)"
    }
}
