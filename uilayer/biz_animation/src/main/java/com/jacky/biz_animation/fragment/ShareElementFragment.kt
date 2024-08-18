package com.jacky.biz_animation.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.transition.TransitionInflater
import com.jacky.biz_animation.R
import com.jacky.bizcommon.ui.BaseFragment
import kotlin.concurrent.fixedRateTimer


class ShareElementFragment : BaseFragment() {

    private lateinit var btn: Button
    private lateinit var shareView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_share_element, container, false)
        initViews(rootView)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move);
        // exitTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.fade);
    }

    override fun initViews(view: View?) {
        view?.let {
            btn = it.findViewById(R.id.btn)
            shareView = it.findViewById(R.id.shareImage)
            btn.setOnClickListener {
                val targetFragment = TargetShareElementFragment()
                // 设置目标fragment中共享元素的转场动画
                // targetFragment.sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
                // targetFragment.sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
                // 进入动画
                targetFragment.sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.change_bounds)
                // 退出目标fragment的动画
                targetFragment.sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.change_bounds)
                parentFragmentManager.beginTransaction()
                    // 共享元素转场动画，必须使用replace
                    .replace(com.jacky.bizcommon.R.id.fg_container, targetFragment)
                    .addSharedElement(shareView, "xixi")
                    .addToBackStack(TargetShareElementFragment::class.java.name)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) // onCreateAnimation/onCreateAnimator动画接收的transit参数
                    .commit()
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        // fragment被添加到fragment堆栈中
        var animation: Animation? = null
        // transit 参数 用于指示 Fragment 的过渡类型，如打开、关闭或淡入淡出。
        // transit 参数是在 FragmentTransaction中设置的，通过调用 setTransition 方法。
        /**
         * transit 参数是一个整数值，表示不同类型的 Fragment 过渡。它可以有以下几种预定义的类型：
         *
         * 	1.	FragmentTransaction.TRANSIT_NONE (0)
         * 没有过渡动画。
         * 	2.	FragmentTransaction.TRANSIT_FRAGMENT_OPEN (1)
         * 打开新的 Fragment，通常表现为新的 Fragment 推入视图中。
         * 	3.	FragmentTransaction.TRANSIT_FRAGMENT_CLOSE (2)
         * 关闭 Fragment，通常表现为 Fragment 从视图中退出。
         * 	4.	FragmentTransaction.TRANSIT_FRAGMENT_FADE (3)
         * Fragment 之间的淡入淡出过渡。
         */
        when (transit) {
            FragmentTransaction.TRANSIT_FRAGMENT_OPEN -> {
                animation = if (enter) {
                    TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    )
                } else {
                    TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    )
                }
            }
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE -> {
                // fragment从堆栈中移除
                animation = if (enter) {
                    TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    )
                } else {
                    TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                    )
                }
            }
            FragmentTransaction.TRANSIT_FRAGMENT_FADE -> {

            }
        }
        if (animation == null) {
            animation = TranslateAnimation(0f, 0f, 0f, 0f)
        }
        animation.duration = 500
        return animation
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        if (enter) {
            // 创建进场动画器
            val animator = ObjectAnimator.ofFloat(this, "translationX", 1000f, 0f)
            animator.setDuration(500)
            return animator
        } else {
            // 创建退场动画器
            val animator = ObjectAnimator.ofFloat(this, "translationX", 0f, -1000f)
            animator.setDuration(500)
            return animator
        }
    }
}