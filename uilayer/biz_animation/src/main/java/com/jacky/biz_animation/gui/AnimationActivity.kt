package com.jacky.biz_animation.gui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.biz_animation.fragment.PropertyAnimatorFragment
import com.jacky.biz_animation.fragment.ViewAnimationFragment
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.bizcommon.ui.VerticalListActivity

@Route(group = RouterConstant.Page.GROUP_NAME, path = RouterConstant.Page.ANIMATION_ACTIVITY)
class AnimationActivity : VerticalListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
    }

    private fun setListener() {
        listAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                showFragment(getListData()[position].second as Fragment)
            }
        }
    }

    override fun getListData(): MutableList<Pair<String, Any>> {
        return mutableListOf(
            Pair("View动画演示", ViewAnimationFragment()),
            Pair("Property动画演示", PropertyAnimatorFragment()),
        )
    }
}