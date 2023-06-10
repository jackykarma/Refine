package com.jacky.foundation.ui.timeline.decoration

/**
 * 单个Item装饰内容的布局信息
 * layouter中需要保存必要的装饰内容布局信息，以支持点击事件检查，比如月视图卡片上需要显示一级标题、二级标题、箭头、点击热区等。
 */
class BaseItemDecoration(private val index: Int) {
    /**
     * decoration的整体透明度
     */
    var alpha: Float = 1f
}
