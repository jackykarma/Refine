package com.jacky.foundation.ui.timeline.gesture

import kotlin.math.abs

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/7, jacky.li, Create file
 * @since 2023/6/7
 * @version v1.0.00
 */
data class Smoother constructor(
    private val factor: Float,
    private val error: Float,
) {
    var currentValue: Float = 0f
    var destValue: Float = 0f
    private var isByPassed: Boolean = false

    fun forceFinish() {
        currentValue = destValue
    }

    fun smooth(): Boolean {
        if (isByPassed) {
            currentValue = destValue
            return false
        }
        val targetValue = currentValue + (destValue - currentValue) * factor
        // 已经到达目标值，smooth应该停止，没有更多帧了
        return if (targetValue.compareTo(currentValue) == 0) {
            currentValue = destValue
            false
        } else {
            currentValue = targetValue
            if (abs(destValue - currentValue) > error) {
                true
            } else {
                currentValue = destValue
                false
            }
        }
    }
}