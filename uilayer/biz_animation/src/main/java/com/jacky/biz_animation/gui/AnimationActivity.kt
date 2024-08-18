package com.jacky.biz_animation.gui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.biz_animation.R
import com.jacky.biz_animation.fragment.PropertyAnimatorFragment
import com.jacky.biz_animation.fragment.ShareElementFragment
import com.jacky.biz_animation.fragment.ViewAnimationFragment
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.bizcommon.ui.VerticalListActivity
import com.jacky.foundation.log.HiLog
import com.jacky.foundation.log.HiLogType

@Route(group = RouterConstant.Page.GROUP_NAME, path = RouterConstant.Page.ANIMATION_ACTIVITY)
class AnimationActivity : VerticalListActivity() {

    private lateinit var shareView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HiLog.d(TAG, HiLogType.D, "onCreate")
        setListener()
    }

    override fun onResume() {
        super.onResume()
        HiLog.d(TAG, HiLogType.D, "onResume")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        HiLog.d(TAG, HiLogType.D, "onAttachedToWindow")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        HiLog.d(TAG, HiLogType.D, "onWindowFocusChanged hasFocus:$hasFocus")
    }

    private fun dynamicAddShareView() {
        shareView = layoutInflater.inflate(R.layout.anim_image, null)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        layoutParams.bottomMargin = 72
        shareView.transitionName = "shishi"
        shareView.id = View.generateViewId()
        addContentView(shareView, layoutParams)
    }

    private fun setListener() {
        listAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (getListData()[position].second == null) {
                    dynamicAddShareView()
                    startActivity(
                        Intent(this@AnimationActivity, ShareElementActivity::class.java),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AnimationActivity, shareView, "shishi").toBundle())
                } else {
                    showFragment(getListData()[position].second as Fragment)
                }
            }
        }
    }

    override fun getListData(): MutableList<Pair<String, Any?>> {
        return mutableListOf(
            Pair("View动画演示", ViewAnimationFragment()),
            Pair("Property动画演示", PropertyAnimatorFragment()),
            Pair("演示Activity过度动画+共享元素", null),
            Pair("演示Fragment过度动画+共享元素", ShareElementFragment())
        )
    }

    companion object {
        private const val TAG = "AnimationActivity"
    }
}