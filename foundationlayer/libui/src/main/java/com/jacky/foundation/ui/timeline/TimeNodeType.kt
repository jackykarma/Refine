package com.jacky.foundation.ui.timeline

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/9, jacky.li, Create file
 * @since 2023/6/9
 * @version v1.0.00
 *
 * FIXME:怎么没有精选月、日视图的放大版
 */
enum class TimeNodeType {
    YEAR,
    MONTH,
    DAY,
    PICKED_DAY,
    PICKED_YEAR;

    companion object {
        @JvmStatic
        fun columnOfType(type: TimeNodeType): String {
            return when (type) {
                DAY, PICKED_DAY -> "day" // 对应数据库中哪个列（字段）的数据
                MONTH -> "month"
                YEAR, PICKED_YEAR -> "year"
            }
        }
    }
}