package com.jacky.biz_animation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.Animation.REVERSE
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.jacky.biz_animation.R
import com.jacky.bizcommon.ui.BaseFragment

class ViewAnimationFragment : BaseFragment() {

    private lateinit var btn: Button
    private lateinit var layout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fg_view_animation, container, false)
        initViews(rootView)
        return rootView
    }

    override fun initViews(view: View?) {
        view?.let {
            layout = it.findViewById(R.id.layout)
            // val layoutAnimation = RotateAnimation(0f, 270f)
            // layout.animation = layoutAnimation
            // layoutAnimation.duration = 2000
            // layoutAnimation.repeatCount = -1
            // layoutAnimation.start()


            btn = it.findViewById(R.id.btn)
            // 动画放到setOnClickListener无法得到执行
            // 无效
            // btn.setOnClickListener {
            //     // Handler(Looper.getMainLooper()).post {
            //     //     doAnimation(btn)
            //     // }
            //
            // }
            // 无效
            // btn.setOnClickListener { v ->
            //     v?.post {
            //         doAnimation(it)
            //     }
            // }
            // 无效
            // btn.setOnTouchListener { v, event ->
            //     doAnimation(btn)
            //     return@setOnTouchListener true
            // }
            // 无效
            // btn.setOnClickListener {
            //     Handler(Looper.getMainLooper()).postDelayed({ doAnimation(btn) }, 2000)
            // }
            // doAnimation(btn)
            btn.setOnClickListener {
                // View动画无法执行
                // doAnimationSet(layout)
                // 属性动画可以执行
                // btn.animate().setDuration(3000).translationY(500f).start()
                doAnimation(view)
                // doAnimationSet(view)
            }
            // doAnimationSet(layout)
        }
    }

    private fun doAnimationSet(view: View) {
        val rotationAnimation = RotateAnimation(0f, 90f)
        val translateAnimation = TranslateAnimation(0f, 800f, 0f, 900f)
        val alphaAnimation = AlphaAnimation(1f, 0f)
        val set = AnimationSet(true)
        set.addAnimation(alphaAnimation)
        set.addAnimation(rotationAnimation)
        set.addAnimation(translateAnimation)
        // view.animation = set
        // 组合动画执行完成以后回到原来的位置
        set.fillAfter = false
        set.duration = 8000
        set.repeatMode = Animation.RESTART
        set.repeatCount = -1
        // 视图动画的正确启动方式是通过视图的 startAnimation() 方法，而不是直接调用动画对象的 start() 方法。
        // 如果使用动画的start方法，那么在视图的onClickListener中动画将无法正常执行
        view.startAnimation(set)
        // set.start()
    }

    private fun doAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)
        animation.duration = 100
        // 动画结束后View是否停留在结束位置。
        animation.fillAfter = true
        // 动画绑定到View
        // view.animation = animation
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                Toast.makeText(context, "动画开始", Toast.LENGTH_SHORT).show()
            }

            override fun onAnimationEnd(animation: Animation?) {
                Toast.makeText(context, "动画结束", Toast.LENGTH_SHORT).show()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                Toast.makeText(context, "动画重复", Toast.LENGTH_SHORT).show()
            }
        })
        animation.repeatMode = Animation.REVERSE
        animation.repeatCount = -1
        animation.interpolator = LinearInterpolator()
        // 动画开始
        // animation.start()
        view.startAnimation(animation)
    }
}