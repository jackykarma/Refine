package com.jacky.biz_animation.fragment

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jacky.biz_animation.R
import com.jacky.bizcommon.ui.BaseFragment
import com.jacky.foundation.log.HiLog
import com.jacky.foundation.log.HiLogType

class PropertyAnimatorFragment : BaseFragment() {

    private lateinit var xixi: ImageView
    private lateinit var shishi: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fg_property_animator, container, false)
        initViews(rootView)
        return rootView
    }

    override fun initViews(view: View?) {
        view?.let {
            xixi = it.findViewById(R.id.xixi)
            shishi = it.findViewById(R.id.shsihi)
            shishi.setImageResource(R.drawable.image1)
            xixi.setImageResource(R.drawable.image6)

            // animator set
            // playXixiAnimation()
            // playShishiAnimation()
            // view property animator
            // viewPropertyAnimator()
            // value animator
            valueEvaluator()
        }
    }

    private fun viewPropertyAnimator() {
        xixi.animate().translationX(-500f).setDuration(300).setListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                xixi.animate().rotation(360f).setDuration(500).start()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        }).start()
        shishi.animate().translationX(500f).setDuration(300).setListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                shishi.animate().rotation(-360f).setDuration(500).start()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        }).start()
    }

    private fun playXixiAnimation() {
        val animatorSet = AnimatorSet()
        val rotateX = ObjectAnimator.ofFloat(xixi, "rotationX", 0f, 360f)
        val translateX = ObjectAnimator.ofFloat(xixi, "translationX", 0f, -600f)
        val alpha = ObjectAnimator.ofFloat(xixi, "alpha", 1f, 0.2f)
        animatorSet.setDuration(1000)
        // 将当前动画traslateX插入到rotateX之前, alpha动画之后，因此顺序就是alpha->translateX->rotateX
        animatorSet.play(translateX).before(rotateX).after(alpha)
        animatorSet.start()
    }

    private fun playShishiAnimation() {
        val animatorSet = AnimatorSet()
        val rotateX = ObjectAnimator.ofFloat(shishi, "rotationX", 0f, -360f)
        val translateX = ObjectAnimator.ofFloat(shishi, "translationX", 0f, 600f)
        animatorSet.setDuration(1000)
        // 无效
        // animatorSet.childAnimations.addAll(arrayListOf(translateX, rotateX))
        // animatorSet.playSequentially()
        animatorSet.play(translateX).before(rotateX)
        animatorSet.start()
    }

    private fun valueEvaluator() {
        val valueAnimator = ValueAnimator.ofFloat(1.0f, 0.1f)
        valueAnimator.setDuration(2000)
        // 设置当前的进度百分比，那么计算将从这一进度对应value开始，0.81450343
        valueAnimator.setCurrentFraction(0.3f)
        valueAnimator.setEvaluator(object : TypeEvaluator<Float> {
            override fun evaluate(fraction: Float, startValue: Float?, endValue: Float?): Float {
                // duration时间决定input的切分粒度(2000ms，一帧16.6ms，那么切分2000/16.6次）；input又通过差值器计算出时间对应的插值fraction
                // 而Evaluator又可以根据插值fraction、动画的起始值、终止值计算出最终值.
                // 如此时间到最终值就建立了一套计算关系。
                // 无论怎样，计算value的方式由你定义，怎样写都可以。最终值如何变化完全由你决定
                HiLog.d(TAG, HiLogType.D, "fraction = $fraction")
                // return startValue!! - fraction * abs(startValue - endValue!!)
                return fraction
            }
        })
        // Interpolator定义如何计算动画中的对象属性值与时间的关系函数。
        valueAnimator.setInterpolator(object : TimeInterpolator {
            // 输入的是动画进度，返回的是fraction
            override fun getInterpolation(input: Float): Float {
                // input在0到1.0f，表示当前动画的进度
                // A value between 0 and 1.0 indicating our current point in the animation where 0 represents the start and 1.0 represents the end
                // 返回值：差值
                HiLog.d(TAG, HiLogType.D, "input = $input")
                // 此处乘以2，那么evaluate方法中的fraction就是从0.6f到2.0f
                // return input * 2
                // 默认情况下，evalute方法中的fraction就是从0.3f到1.0f
                return input
            }
        })
        valueAnimator.addUpdateListener {
            // 根据duration的进度百分比，不断计算出值，你可以将此值再作为某个对象的属性值，更新到对象；
            // 比如更新到view的透明度属性或是其他float的属性都行
            val value = it.animatedValue as Float
            HiLog.d(TAG, "value = $value")
            xixi.alpha = value
            shishi.alpha = value
        }
        // 开始计算
        valueAnimator.start()
    }

    companion object {
        private const val TAG = "PropertyAnimatorFragmen"
    }
}