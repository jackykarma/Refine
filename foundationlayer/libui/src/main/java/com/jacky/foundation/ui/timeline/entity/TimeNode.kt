package com.jacky.foundation.ui.timeline.entity

import com.jacky.foundationimpl.TimeUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 *
 * 时间结点实体类，记录时间节点包含的所有信息
 */
class TimeNode(

    /**
     * 此时间结点的日期字符串（如：2020年8月 -- 202008）
     */
    val id: String,

    /**
     * 此时间结点的全局位置
     */
    val position: Int,

    /**
     * 此时间节点包含的时间段
     */
    val timeRange: LongRange,

    /**
     * 此时间节点的日期时间戳
     */
    val timestamp: Long,

    /**
     * 此时间节点包含的Item下标范围
     */
    val itemRange: IntRange,

    /**
     * 此时间节点包含的block下标范围
     */
    val blockRange: IntRange,

    /**
     * 此时间段所有地理信息
     */
    var locationList: List<String>? = null,

    /**
     * 此时间节点的位置信息，用于是否常住地判断
     */
    var geoRoute: ConfigAddress? = null,

    /**
     * 此时间节点的节日
     */
    val festival: String? = null
) {
    /**
     * 此时间节点可显示的照片个数，被过滤的照片不会统计到itemCount中
     * 时间轴没有被过滤的照片，itemCount=totalCount
     * 精选画廊有被过滤的照片，itemCount=totalCount - 过滤掉不可显示的照片数
     */
    var itemCount = itemRange.count()

    /**
     * 此时间节点下照片总个数，被过滤的照片也会统计到totalCount中
     * 时间轴和画廊页totalCount是相等的，都是等于时间轴此时间节点的个数
     */
    val totalCount = itemRange.count()

    /**
     * 当前节点的封面Item索引
     */
    var coverIndex: Int = itemRange.first
        set(value) {
            if (value !in itemRange) {
                return
            }
            field = value
        }

    /**
     * 当前节点的综合评分最高的Item索引
     */
    var highestScoreIndex: Int = -1

    /**
     * 当前节点是否是大图合并的结点
     */
    val isPicMerge: Boolean
        get() = TimeUtils.isSameDay(timeRange.first, timeRange.last)

    /**
     * 隐藏不显示的Item数量
     * hideItemCount
     */
    val remainCount: Int
        get() = totalCount - itemRange.count()

    /**
     * 结点额外信息，方便存放一些页面差异化信息
     */
    val extraInfo = ConcurrentHashMap<String, Any>()

    /**
     * 清除附加信息
     */
    fun resetExtraInfo() {
        extraInfo.remove(EXTRA_KEY_TITLE)
        extraInfo.remove(EXTRA_KEY_SUB_TITLE)
        extraInfo.remove(EXTRA_KEY_TIME_TITLE)
        extraInfo.remove(EXTRA_KEY_LOCATION_TITLE)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeNode

        if (id != other.id) return false
        if (position != other.position) return false
        if (timeRange != other.timeRange) return false
        if (timestamp != other.timestamp) return false
        if (itemRange != other.itemRange) return false
        if (blockRange != other.blockRange) return false
        if (locationList != other.locationList) return false
        if (geoRoute != other.geoRoute) return false
        if (festival != other.festival) return false
        if (itemCount != other.itemCount) return false
        if (totalCount != other.totalCount) return false
        if (coverIndex != other.coverIndex) return false
        if (highestScoreIndex != other.highestScoreIndex) return false
        if (extraInfo != other.extraInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + position
        result = 31 * result + timeRange.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + itemRange.hashCode()
        result = 31 * result + blockRange.hashCode()
        result = 31 * result + (locationList?.hashCode() ?: 0)
        result = 31 * result + (geoRoute?.hashCode() ?: 0)
        result = 31 * result + (festival?.hashCode() ?: 0)
        result = 31 * result + itemCount
        result = 31 * result + totalCount
        result = 31 * result + coverIndex
        result = 31 * result + highestScoreIndex
        result = 31 * result + extraInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "TimeNode(id='$id', position=$position, timeRange=$timeRange, " +
                "timestamp=$timestamp, itemRange=$itemRange, blockRange=$blockRange, " +
                "locationList=$locationList, geoRoute=$geoRoute, festival=$festival, " +
                "itemCount=$itemCount, totalCount=$totalCount, coverIndex=$coverIndex, " +
                "highestScoreIndex=$highestScoreIndex, extraInfo=$extraInfo)"
    }

    companion object {
        /**
         * 日期（20230515）中的年字符串索引
         */
        const val END_INDEX_YEAR = 4

        /**
         * 日期（20230515）中的月字符串索引
         */
        const val END_INDEX_MONTH = 6

        const val EXTRA_KEY_SELECTION = "nodeSelection"
        const val EXTRA_KEY_TIME_TITLE = "timeTitle"
        const val EXTRA_KEY_LOCATION_TITLE = "locationTitle"
        const val EXTRA_KEY_TITLE = "title"
        const val EXTRA_KEY_SUB_TITLE = "subTitle"

        @JvmStatic
        fun isTimeNodesEquals(first: List<TimeNode>, last: List<TimeNode>): Boolean {
            if (first.size != last.size) return false

            for (index in first.indices) {
                if (!first[index].equals(last[index])) {
                    return false
                }
            }

            return true
        }
    }
}