package com.jacky.foundationimpl

import java.util.Calendar

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 */
object TimeUtils {

    /**
     * 判断2个时间是否是同一天
     */
    fun isSameDay(time1: Long, time2: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time1
        val year1 = calendar.get(Calendar.YEAR)
        val day1 = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = time2
        val year2 = calendar.get(Calendar.YEAR)
        val day2 = calendar.get(Calendar.DAY_OF_YEAR)
        return (year1 == year2) && (day1 == day2)
    }
}