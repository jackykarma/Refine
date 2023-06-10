package com.jacky.foundation.ui.timeline.gesture

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/7, jacky.li, Create file
 * @since 2023/6/7
 * @version v1.0.00
 *
 * 缩放旋转手势检测器
 * 参考Android官方的手势检测器GestureDetector的写法，实现系统内置没有提供的手势检测器。
 */
class ScaleRotateGestureDetector(private val onScaleRotateGestureListener: OnScaleRotateGestureListener) {

    private var handler: Handler = InternalHandler()

    @SuppressLint("HandlerLeak")
    inner class InternalHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HANDLE_REQUEST_INTERPOLATER_EVENT -> fireEventProcess(PROCESS_SCALE_ROTATE)
                else -> {}
            }
        }
    }

    private fun fireEventProcess(fakeEvent: Int): Boolean {
        when (fakeEvent) {
            PROCESS_SCALE_ROTATE_BEGIN -> {
                var rotatePivotX = 0f
                var rotatePivotY = 0f
                var scalePivotX = 0f
                var scalePivotY = 0f
                if (isCenterReferredByScale) {
                    rotatePivotX = pointerCenter.x
                    rotatePivotY = pointerCenter.y
                } else {
                    rotatePivotX = pointerMain.x
                    rotatePivotY = pointerMain.y
                }
                if (isCenterReferredByRotate) {
                    scalePivotX = pointerCenter.x
                    scalePivotY = pointerCenter.y
                } else {
                    scalePivotX = pointerMain.x
                    scalePivotY = pointerMain.y
                }

                val scale = computeScaleFactor()
                val rotate = computeDeltaRotateAngle()
                rotateSmoother.destValue = rotate
                rotateSmoother.forceFinish()
                scaleSmoother.destValue = scale
                scaleSmoother.forceFinish()

                return onScaleRotateGestureListener.onScaleRotateBegin(
                    rotatePivotX,
                    rotatePivotY,
                    if (isRotateSmoothEnabled) rotateSmoother.currentValue else rotate,
                    scalePivotX,
                    scalePivotY,
                    if (isScaleSmoothEnabled) scaleSmoother.currentValue else scale,
                    this
                )
            }
            PROCESS_SCALE_ROTATE -> {
                var rotatePivotX = 0f
                var rotatePivotY = 0f
                var scalePivotX = 0f
                var scalePivotY = 0f
                if (isCenterReferredByScale) {
                    rotatePivotX = pointerCenter.x
                    rotatePivotY = pointerCenter.y
                } else {
                    rotatePivotX = pointerMain.x
                    rotatePivotY = pointerMain.y
                }
                if (isCenterReferredByRotate) {
                    scalePivotX = pointerCenter.x
                    scalePivotY = pointerCenter.y
                } else {
                    scalePivotX = pointerMain.x
                    scalePivotY = pointerMain.y
                }

                val scale = computeScaleFactor()
                val rotate = computeDeltaRotateAngle()
                rotateSmoother.destValue = rotate
                scaleSmoother.destValue = scale
                val hasMoreFrames = rotateSmoother.smooth() || scaleSmoother.smooth()

                val state =  onScaleRotateGestureListener.onScaleRotate(
                    rotatePivotX,
                    rotatePivotY,
                    if (isRotateSmoothEnabled) rotateSmoother.currentValue else rotate,
                    scalePivotX,
                    scalePivotY,
                    if (isScaleSmoothEnabled) scaleSmoother.currentValue else scale,
                    this
                )

                if (hasMoreFrames) {
                    handler.sendEmptyMessage(HANDLE_REQUEST_INTERPOLATER_EVENT)
                }

                return state
            }
            PROCESS_SCALE_ROTATE_END -> {
                var rotatePivotX = 0f
                var rotatePivotY = 0f
                var scalePivotX = 0f
                var scalePivotY = 0f
                if (isCenterReferredByScale) {
                    rotatePivotX = pointerCenter.x
                    rotatePivotY = pointerCenter.y
                } else {
                    rotatePivotX = pointerMain.x
                    rotatePivotY = pointerMain.y
                }
                if (isCenterReferredByRotate) {
                    scalePivotX = pointerCenter.x
                    scalePivotY = pointerCenter.y
                } else {
                    scalePivotX = pointerMain.x
                    scalePivotY = pointerMain.y
                }

                val scale = computeScaleFactor()
                val rotate = computeDeltaRotateAngle()
                rotateSmoother.destValue = rotate
                scaleSmoother.destValue = scale
                scaleSmoother.smooth()
                rotateSmoother.smooth()

                val state = onScaleRotateGestureListener.onScaleRotateBegin(
                    rotatePivotX,
                    rotatePivotY,
                    if (isRotateSmoothEnabled) rotateSmoother.currentValue else rotate,
                    scalePivotX,
                    scalePivotY,
                    if (isScaleSmoothEnabled) scaleSmoother.currentValue else scale,
                    this
                )

                handler.removeMessages(HANDLE_REQUEST_INTERPOLATER_EVENT)
                return state
            }

            else -> return false
        }
    }

    /**
     * 初始缩放手势的距离
     */
    private var initScaleDistance: Float = 0f

    /**
     * 上次缩放手势的距离
     */
    private var prevScaleDistance: Float = 0f

    /**
     * 当前缩放手势的距离
     */
    private var currentScaleDistance: Float = 0f

    private var isRotateSmoothEnabled = false
    private var isScaleSmoothEnabled = false
    private var isCenterReferredByRotate = false
    private var isCenterReferredByScale = false

    private var initRotateRadian: Float = 0f
    private var currentRotateRadian: Float = 0f

    /**
     * 触控手指的数量
     */
    private var pointerCount = 0

    /**
     * 两个触控点的中心位置
     */
    private var pointerCenter = PointF()

    /**
     * 主触摸点
     */
    private var pointerMain = PointF()

    /**
     * 次触摸点
     */
    private var pointerSecondary = PointF()

    private var rotateSmoother = Smoother(ROTATE_SMOOTH_FACTOR, ROTATE_SMOOTH_ERROR)
    private var scaleSmoother = Smoother(SCALE_SMOOTH_FACTOR, SCALE_SMOOTH_ERROR)


    fun getDeltaScaleDistance(): Float {
        return currentScaleDistance - initScaleDistance
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK
        var state = false
        pointerCount = event.pointerCount
        confirmPointerPosition(event)

        when (action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (pointerCount == 2) {
                    computeInitializeScaleDistance()
                    computeInitializeRotateAngle()
                    state = fireEventProcess(PROCESS_SCALE_ROTATE_BEGIN)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerCount >= 2) {
                    computeCurrentScaleDistance()
                    computeCurrentRotateAngle()
                    state = fireEventProcess(PROCESS_SCALE_ROTATE)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (pointerCount == 2) {
                    computeCurrentRotateAngle()
                    computeCurrentScaleDistance()
                    state = fireEventProcess(PROCESS_SCALE_ROTATE_END)
                }
            }
            MotionEvent.ACTION_UP -> {
                resetEvent()
            }
            MotionEvent.ACTION_CANCEL -> {
                computeCurrentScaleDistance()
                computeCurrentScaleDistance()
                state = fireEventProcess(PROCESS_SCALE_ROTATE_END)
                resetEvent()
            }
            else -> {}
        }

        return state
    }

    /**
     * 计算初始的手势缩放距离（两指之间的距离）
     */
    private fun computeInitializeScaleDistance() {
        val deltaX = pointerSecondary.x - pointerMain.x
        val deltaY = pointerSecondary.y - pointerMain.y
        initScaleDistance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
        prevScaleDistance = initScaleDistance
        currentScaleDistance = initScaleDistance
    }

    /**
     * 计算当前缩放距离（两指之间的距离）
     */
    private fun computeCurrentScaleDistance() {
        val deltaX = pointerSecondary.x - pointerMain.x
        val deltaY = pointerSecondary.y - pointerMain.y
        prevScaleDistance = currentScaleDistance
        currentScaleDistance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
    }

    /**
     * 计算当前相对上次的缩放倍数
     */
    private fun computeDeltaScaleFactor(): Float {
        // 缩放倍数
        var scaleFactor = currentScaleDistance / prevScaleDistance
        scaleFactor = if (scaleFactor < MIN_SCALE_FACTOR) MIN_SCALE_FACTOR else scaleFactor
        return scaleFactor
    }

    /**
     * 计算当前相比初始手势缩放的倍数
     */
    private fun computeScaleFactor(): Float {
        // 缩放倍数
        var scaleFactor = currentScaleDistance / initScaleDistance
        scaleFactor = if (scaleFactor < MIN_SCALE_FACTOR) MIN_SCALE_FACTOR else scaleFactor
        return scaleFactor
    }

    /**
     * 计算初始的旋转角度
     */
    private fun computeInitializeRotateAngle() {
        val deltaX = pointerSecondary.x - pointerMain.x
        val deltaY = pointerSecondary.y - pointerMain.y
        initRotateRadian = atan2(deltaY.toDouble(), deltaX.toDouble()).toFloat()
        currentRotateRadian = initRotateRadian
    }

    /**
     * 计算当前的旋转角度
     */
    private fun computeCurrentRotateAngle() {
        val deltaX = pointerSecondary.x - pointerMain.x
        val deltaY = pointerSecondary.y - pointerMain.y
        currentRotateRadian = atan2(deltaY.toDouble(), deltaX.toDouble()).toFloat()
    }

    private fun computeDeltaRotateAngle(): Float {
        return Math.toDegrees(computeDeltaRotateRadian().toDouble()).toFloat()
    }

    private fun computeDeltaRotateRadian(): Float {
        return currentRotateRadian - initRotateRadian
    }

    private fun resetEvent() {
        pointerCount = 0
        pointerMain.x = INVALID_POINTER_POSITION
        pointerMain.y = INVALID_POINTER_POSITION
        pointerSecondary.x = INVALID_POINTER_POSITION
        pointerSecondary.y = INVALID_POINTER_POSITION
    }

    private fun confirmPointerPosition(event: MotionEvent) {
        val pointerCount = event.pointerCount
        if (pointerCount == 1) {
            pointerMain.x = event.getX(0)
            pointerMain.y = event.getY(0)
            pointerSecondary.x = INVALID_POINTER_POSITION
            pointerSecondary.y = INVALID_POINTER_POSITION
            pointerCenter.x = pointerMain.x
            pointerCenter.y = pointerMain.y
        } else if (pointerCount >= 2) {
            pointerMain.x = event.getX(0)
            pointerMain.y = event.getY(0)
            pointerSecondary.x = event.getX(1)
            pointerSecondary.y = event.getY(1)
            pointerCenter.x = pointerMain.x + (pointerSecondary.x - pointerMain.x) * HALF_ONE_POINT
            pointerCenter.y = pointerMain.y + (pointerSecondary.y - pointerMain.y) * HALF_ONE_POINT
        } else {
            pointerMain.x = INVALID_POINTER_POSITION
            pointerMain.y = INVALID_POINTER_POSITION
            pointerSecondary.x = INVALID_POINTER_POSITION
            pointerSecondary.y = INVALID_POINTER_POSITION
            pointerCenter.x = INVALID_POINTER_POSITION
            pointerCenter.y = INVALID_POINTER_POSITION
        }
    }

    companion object {
        private const val MIN_SCALE_FACTOR = 0.01f
        private const val HALF_ONE_POINT = 0.5f
        private const val ROTATE_SMOOTH_FACTOR = 5E-1F
        private const val ROTATE_SMOOTH_ERROR = 5E-2F
        private const val SCALE_SMOOTH_FACTOR = 5E-1F
        private const val SCALE_SMOOTH_ERROR = 5E-2F
        private const val INVALID_POINTER_POSITION = -1F
        private const val PROCESS_SCALE_ROTATE_BEGIN = 0X0001
        private const val PROCESS_SCALE_ROTATE = 0X0002
        private const val PROCESS_SCALE_ROTATE_END = 0X0003
        private const val HANDLE_REQUEST_INTERPOLATER_EVENT = 0x0004;
    }
}

/**
 * 缩放旋转手势监听器
 */
interface OnScaleRotateGestureListener {
    /**
     * 缩放旋转手势开始
     * @param scalePivotX 缩放的中心点X的坐标
     * @param scalePivotY 缩放的中心点Y的坐标
     * @param angle 角度
     * @param rotatePivotX 旋转的中心点X的坐标
     * @param rotatePivotY 旋转的中心点Y的坐标
     * @param detector 缩放旋转手势检测器
     */
    fun onScaleRotateBegin(
        scalePivotX: Float, scalePivotY: Float, angle: Float,
        rotatePivotX: Float, rotatePivotY: Float, scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean

    fun onScaleRotate(
        scalePivotX: Float, scalePivotY: Float, angle: Float,
        rotatePivotX: Float, rotatePivotY: Float, scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean

    fun onScaleRotateEnd(
        scalePivotX: Float, scalePivotY: Float, angle: Float,
        rotatePivotX: Float, rotatePivotY: Float, scale: Float,
        detector: ScaleRotateGestureDetector
    ): Boolean
}