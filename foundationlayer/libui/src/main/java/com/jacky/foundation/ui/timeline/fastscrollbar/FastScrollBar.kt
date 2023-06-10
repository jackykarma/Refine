package com.jacky.foundation.ui.timeline.fastscrollbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import com.jacky.foundation.ui.R

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description 全屏的快速滚动条
 * @author jacky.li
 * 2023/6/7, jacky.li, Create file
 * @since 2023/6/7
 * @version v1.0.00
 */
class FastScrollBar constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_fast_scrollbar, this, true)
    }

    /**
     * 背景控件
     */
    private val backgroundImage: ImageView by lazy {
        findViewById(R.id.fast_scrollbar_bg)
    }

    /**
     * 内容控件
     */
    private val contentImage: ImageView by lazy {
        findViewById(R.id.fast_scrollbar_content)
    }

    /**
     * 快滑条动画集
     */
    private val scrollbarAnimatorSet: AnimatorSet by lazy {
        val animators: MutableList<Animator> = ArrayList<Animator>().apply {
            add(
                ObjectAnimator.ofFloat(backgroundImage, SCALE_X, 1F, SCALE_X_VALUE)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(backgroundImage, SCALE_Y, 1F, SCALE_Y_VALUE)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(backgroundImage, TRANSLATION_X, 0F, animationTransX)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(contentImage, TRANSLATION_X, 0F, animationTransX)
                    .setScaleTranslateConfig()
            )
        }
        AnimatorSet().apply {
            playTogether(animators)
        }
    }

    private val reverseScrollbarAnimatorSet: AnimatorSet by lazy {
        val animators: MutableList<Animator> = ArrayList<Animator>().apply {
            add(
                ObjectAnimator.ofFloat(backgroundImage, SCALE_X, SCALE_X_VALUE, 1F)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(backgroundImage, SCALE_Y, SCALE_Y_VALUE, 1F)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(backgroundImage, TRANSLATION_X, animationTransX, 0F)
                    .setScaleTranslateConfig()
            )
            add(
                ObjectAnimator.ofFloat(contentImage, TRANSLATION_X, animationTransX, 0F)
                    .setScaleTranslateConfig()
            )
        }
        AnimatorSet().apply {
            playTogether(animators)
        }
    }

    private val animationTransX =
        -context.resources.getDimensionPixelSize(R.dimen.fast_scrollbar_translation_x).toFloat()

    private fun ObjectAnimator.setScaleTranslateConfig() = apply {
        interpolator = SCALE_INTERPOLATOR
        duration = SCALE_ANIMATION_DURING_TIME
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            doShowAnimation()
        } else {
            doHideAnimation()
        }
    }

    private fun doHideAnimation() {
        if (!isVisible) return
        this.animate().apply {
            alpha(0F)
            duration = ALPHA_ANIMATION_DURING_TIME
            interpolator = ALPHA_INTERPOLATOR
            withEndAction {
                super.setVisibility(visibility)
            }
        }.start()
    }

    private fun doShowAnimation() {
        if (isVisible) return
        super.setVisibility(VISIBLE)
        this.animate().apply {
            alpha(1F)
            duration = ALPHA_ANIMATION_DURING_TIME
            interpolator = ALPHA_INTERPOLATOR
        }.start()
    }

    /**
     * 设置可见性-但不启动动画
     */
    fun setVisibilityWithoutAnimation(visibility: Int) {
        super.setVisibility(visibility)
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        super.dispatchSetPressed(pressed)
        // 按压动画
        doTouchAnimation(!pressed)
    }

    /**
     * 执行触摸动画
     */
    private fun doTouchAnimation(isReverse: Boolean) {
        if (isReverse) {
            scrollbarAnimatorSet.cancel()
            reverseScrollbarAnimatorSet.start()
        } else {
            reverseScrollbarAnimatorSet.cancel()
            scrollbarAnimatorSet.start()
        }
    }

    /**
     * 取消所有动画
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAllAnimators()
    }

    private fun cancelAllAnimators() {
        scrollbarAnimatorSet.cancel()
        reverseScrollbarAnimatorSet.cancel()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        when(ev?.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> isPressed = false
            MotionEvent.ACTION_DOWN -> isPressed = true
        }
        return true
    }

    companion object {
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
        private const val TRANSLATION_X = "translationX"
        private const val ALPHA_ANIMATION_DURING_TIME = 350L
        private const val SCALE_ANIMATION_DURING_TIME = 500L
        private const val SCALE_X_VALUE = 1.36F
        private const val SCALE_Y_VALUE = 1.27F
        private val ALPHA_INTERPOLATOR: Interpolator = PathInterpolator(0.33f, 0f, 0.67f, 1f)
        private val SCALE_INTERPOLATOR: Interpolator = PathInterpolator(0.3f, 0f, 0.1f, 1f)
    }
}

