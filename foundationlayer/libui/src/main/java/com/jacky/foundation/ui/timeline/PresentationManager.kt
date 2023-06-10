package com.jacky.foundation.ui.timeline

import com.jacky.foundation.ui.timeline.layouter.OnLayoutListener
import com.jacky.foundation.ui.timeline.presentation.TimelineViewPresentation

/**
 * 管理各个Presentation视图
 */
class PresentationManager(timelineView: TimelineView, layoutListener: OnLayoutListener) {
    lateinit var switchCallback: (String, String) -> Unit

    fun setPresentations(presentations: List<TimelineViewPresentation<*>>, defaultType: String?) {
        TODO("Not yet implemented")
    }

}
