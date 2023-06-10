package com.jacky.foundation.ui.timeline

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 */
val DEFAULT_SELECT_MODE_SPEC by lazy {
    makeSelectModeSpec(
        isSelectMode = false,
        canShowSelectionMode = true
    )
}

private const val CAN_SHOW_SELECTION_SHIFT = 4

fun makeSelectModeSpec(isSelectMode: Boolean, canShowSelectionMode: Boolean = true): Int {
    return isSelectMode.toInt() or (canShowSelectionMode.toInt() shl CAN_SHOW_SELECTION_SHIFT)
}

private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}
