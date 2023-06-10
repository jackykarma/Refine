package com.jacky.foundation.ui.timeline.gesture

import android.content.Context
import android.view.MotionEvent
import com.jacky.foundation.ui.R
import kotlin.math.abs

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/7, jacky.li, Create file
 * @since 2023/6/7
 * @version v1.0.00
 *
 * 捏合手势：Pinching gesture
 *
 * 时间轴捏合手势检测器，规则如下：
 * 1. OverScroll状态禁用捏合手势;
 * 2. 支持Fling过程进入捏合手势状态；
 * 3. 捏合过程增加手指，直接以动画方式结束捏合动作，并消耗整个touch事件；
 * 4. ...
 */
class TimelinePinchGestureDetector constructor(
    context: Context,
    private var zoomListener: OnZoomListener
) : OnScaleRotateGestureListener {

    /**
     * 更新上次距离的阈值
     */
    private val updateLastDistanceThreshold: Int by lazy {
        context.resources.getDimensionPixelSize(R.dimen.timeline_pinch_update_distance_threshold)
    }

    /**
     * 当前的捏合状态
     */
    private var pinchState = PINCH_NONE

    /**
     * 缩放旋转手势-检测器
     */
    private val scaleRotateGestureDetector = ScaleRotateGestureDetector(this)

    /**
     * 上次捏合的时间戳, 用于计算捏合速度
     */
    private var lastPinchTimestamp: Long = 0L

    /**
     * 上次捏合的距离, 用于计算捏合速度
     */
    private var lastPinchDistance: Float = 0f

    /**
     * 上次的距离
     */
    private var lastDistance: Float = 0f

    /**
     * 速度队列
     */
    private var velocityQueue = VelocityQueue(VELOCITY_COUNT)

    /**
     * 用于控制双指操作中一个手松手后不再处理触碰事件的flag标志
     */
    private var isScaleRotateEnded = false

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            pinchState = PINCH_NONE
            isScaleRotateEnded = false
        }

        // 手指数量大于一定值时，停止缩放手势并消耗触摸事件
        if (event.pointerCount > PINCH_POINTER_COUNT_MAX) {
            if (isZooming()) {
                zoomListener.onZoomEnd(
                    clampDistance(scaleRotateGestureDetector.getDeltaScaleDistance()),
                    0f,
                    isZoomIn()
                )
            }
            pinchState = PINCH_FINISHED
            return true
        }
        // 缩放旋转手势的检测交给 ScaleRotateGestureDetector 处理
        var handled = scaleRotateGestureDetector.onTouchEvent(event)
        // 进入捏合状态则直接消费事件，避免滚动
        handled = handled || isPinching()
        // 捏合操作已经结束则消耗整个事件，避免剩下单指触屏时任何滚动
        handled = handled || isPinchFinished()
        return handled
    }

    private fun clampDistance(distance: Float): Float =
        when (pinchState) {
            PINCH_ZOOM_IN -> distance.coerceAtLeast(0f)
            PINCH_ZOOM_OUT -> abs(distance.coerceAtMost(0f))
            else -> 0f
        }

    /**
     * 正在进行捏合缩放过程中
     */
    fun isZooming(): Boolean = (pinchState == PINCH_ZOOM_IN) || (pinchState == PINCH_ZOOM_OUT)
    fun isZoomIn(): Boolean = pinchState == PINCH_ZOOM_IN
    fun isZoomOut(): Boolean = pinchState == PINCH_ZOOM_OUT
    fun isPinching(): Boolean = pinchState != PINCH_NONE
    fun isPinchFinished(): Boolean = pinchState == PINCH_FINISHED

    override fun onScaleRotateBegin(
        scalePivotX: Float,
        scalePivotY: Float,
        angle: Float,
        rotatePivotX: Float,
        rotatePivotY: Float,
        scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean {
        pinchState = PINCH_UNDEFINED
        lastPinchTimestamp = System.currentTimeMillis()
        lastPinchDistance = 0f
        lastDistance = 0f
        velocityQueue.clear()
        return true
    }

    override fun onScaleRotate(
        scalePivotX: Float,
        scalePivotY: Float,
        angle: Float,
        rotatePivotX: Float,
        rotatePivotY: Float,
        scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean {
        if (isScaleRotateEnded) return false
        val zoomDistance = detector.getDeltaScaleDistance()
        if (pinchState == PINCH_UNDEFINED) {
            pinchState = when {
                (zoomDistance - lastDistance) > 0f -> PINCH_ZOOM_IN
                (zoomDistance - lastDistance) < 0f -> PINCH_ZOOM_OUT
                else -> return true
            }
            lastDistance = zoomDistance
            val handled =
                zoomListener.onZoomBegin(scalePivotX.toInt(), scalePivotY.toInt(), isZoomIn())
            velocityQueue.clear()
            if (!handled) {
                pinchState = PINCH_UNDEFINED
            }
        } else if (isZooming()) {
            pinchState = when {
                (zoomDistance - lastDistance) > 0f -> PINCH_ZOOM_IN
                (zoomDistance - lastDistance) < 0f -> PINCH_ZOOM_OUT
                else -> return true
            }

            zoomListener.onZooming(zoomDistance, zoomDistance - lastDistance)

            var curTime = System.currentTimeMillis()
            val deltaPinchTime = (curTime - lastPinchTimestamp).coerceAtLeast(DELTA_TIME_MIN)
            // 采用绝对值，因为现在距离有正负
            val deltaPinchDistance = abs(zoomDistance - lastDistance)
            velocityQueue.add(deltaPinchDistance / deltaPinchTime)
            lastPinchTimestamp = curTime
            if (deltaPinchDistance >= updateLastDistanceThreshold) {
                lastDistance = zoomDistance
            }
        }
        return true
    }

    override fun onScaleRotateEnd(
        scalePivotX: Float,
        scalePivotY: Float,
        angle: Float,
        rotatePivotX: Float,
        rotatePivotY: Float,
        scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean {
        if (isZooming()) {
            val zoomDistance = detector.getDeltaScaleDistance()
            zoomListener.onZoomEnd(zoomDistance, velocityQueue.average(), isZoomIn())
        }
        pinchState = PINCH_FINISHED
        isScaleRotateEnded = true
        return true
    }

    /**
     * 提供给外部来监听捏合手势
     */
    interface OnZoomListener {
        fun onZoomBegin(pivotX: Int, pivotY: Int, isZoomInt: Boolean): Boolean
        fun onZooming(distance: Float, diff: Float)
        fun onZoomEnd(distance: Float, velocity: Float, zoomIn: Boolean)
    }

    /**
     * 速度队列,用于存放速度，然后计算求和和平均值
     */
    inner class VelocityQueue(private val capacity: Int) {
        private val velocities: ArrayDeque<Float> = ArrayDeque(capacity + 1)
        private var sum: Float = 0f

        fun average(): Float {
            return if (velocities.size == 0) 0f
            else sum / velocities.size
        }

        fun add(value: Float) {
            sum += value
            velocities.addLast(value)
            if (velocities.size > capacity) {
                sum -= velocities.removeFirst()
            }
        }

        fun clear() {
            velocities.clear()
            sum = 0f
        }
    }

    companion object {
        private const val TAG = "TimelinePinchGestureDet"
        private const val DELTA_TIME_MIN = 1L

        /**
         * 捏合手势最大支持的手指数量
         */
        private const val PINCH_POINTER_COUNT_MAX = 2

        /**
         * 计算Fling速度时，取最后VELOCITY_COUNT组数据的平均值
         */
        private const val VELOCITY_COUNT = 3

        /**
         * 非捏合状态-改进为enum
         */
        private const val PINCH_NONE = -1

        /**
         * 捏合中状态，但放缩状态未知
         */
        private const val PINCH_UNDEFINED = 0

        /**
         * 捏合中状态，放大
         */
        private const val PINCH_ZOOM_IN = 1

        /**
         * 捏合中状态，缩小
         */
        private const val PINCH_ZOOM_OUT = 2

        /**
         * 捏合结束状态
         */
        private const val PINCH_FINISHED = 3

    }
}