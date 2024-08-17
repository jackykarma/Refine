package com.jacky.biz_animation.gui

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.jacky.biz_animation.R

class ShareElementActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //在需要启动的 activity 中开启动画的特征; 可以不加
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activitiy_share_elment)
        // window.enterTransition = Explode()
        window.exitTransition = Slide()
        window.enterTransition = Fade()
    }
}