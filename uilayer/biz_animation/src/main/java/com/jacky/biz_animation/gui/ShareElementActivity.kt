package com.jacky.biz_animation.gui

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeScroll
import android.transition.ChangeTransform
import android.transition.Explode
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionSet
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.jacky.biz_animation.R

class ShareElementActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在需要启动的 activity 中开启动画的特征; 可以不加
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activitiy_share_elment)
        // window.enterTransition = Explode()
        // 定义窗口进场和退场动画
        window.exitTransition = Explode()
        window.enterTransition = Slide()
        // window.sharedElementExitTransition = Slide()
        // window.sharedElementEnterTransition = Fade()
        // 自定义共享元素转场动画
        window.sharedElementEnterTransition = createShareElementEnterTransition()
        window.sharedElementExitTransition = createShareElementExitTransition()
    }

    // 用代码动态创建过度动画对象，也可以使用xml定义
    private fun createShareElementEnterTransition(): Transition {
        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeBounds().setDuration(10000))
        transitionSet.addTransition(ChangeTransform().setDuration(10000))
        return transitionSet
    }

    private fun createShareElementExitTransition(): Transition {
        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeScroll().setDuration(10000))
        transitionSet.addTransition(ChangeImageTransform().setDuration(10000))
        return transitionSet
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }
}