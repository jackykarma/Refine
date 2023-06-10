package com.jacky.foundation.ui.timeline

interface StateChangeObserver {
    var selectModeSpec: Int

    fun register(listener: StateChangeListener)
}

interface StateChangeListener {
    fun onSelectionModeChanged(selectModeSpec: Int)
    fun onCheckBoxAnimEnableChanged(isAutoScrolling: Boolean)
    fun onMaskVisibleChanged(isMaskVisible: Boolean)
    fun onAccentColorChanged()
    fun onRtlModeChanged(isRtl: Boolean)
}
