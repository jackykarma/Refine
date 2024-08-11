package com.jacky.biz_cusview.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.jacky.biz_cusview.R

/**
 * TODO: document your custom view class.
 */
class CompositeView : View {

    companion object {
        private const val DEFAULT_TEXT_SIZE = 16f
        private const val DEFAULT_RADIUS = 20f
    }

    private var _sampleTextColor: Int = Color.RED
    private var sampleText: String? = null
    private var sampleSrc: Int? = null
    private var sampleRadius: Float = DEFAULT_RADIUS
    private var sampleTextSize: Float = DEFAULT_TEXT_SIZE

    private lateinit var textPaint: TextPaint
    private lateinit var paint: Paint
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    /**
     * The font color
     */
    var sampleTextColor: Int
        get() = _sampleTextColor
        set(value) {
            _sampleTextColor = value
            invalidateTextPaintAndMeasurements()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CompositeView, defStyle, 0
        )

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        paint = Paint()

        sampleSrc = a.getResourceId(R.styleable.CompositeView_sampleSrc,0)
        sampleText = a.getString(R.styleable.CompositeView_sampleText)
        sampleRadius = a.getDimension(R.styleable.CompositeView_sampleRadius, 0f)
        sampleTextColor = a.getColor(R.styleable.CompositeView_sampleTextColor, sampleTextColor)
        sampleTextSize = a.getDimension(R.styleable.CompositeView_sampleTextSize, DEFAULT_TEXT_SIZE)

        a.recycle()

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.textSize = sampleTextSize
            it.color = sampleTextColor
            textWidth = it.measureText(sampleText)
            textHeight = it.fontMetrics.bottom
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun drawText(canvas: Canvas, contentWidth: Int, contentHeight: Int) {
        sampleText?.let {
            // 绘制文本
            canvas.drawText(
                it,
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                textPaint
            )
        }
    }

    private var imageHeight: Int = 0
    private var imageWidth: Int = 0

    private fun drawImage(canvas: Canvas, contentWidth: Int, contentHeight: Int) {
        // Draw the example drawable on top of the text.
        sampleSrc?.let {
            val bitmap = BitmapFactory.decodeResource(resources, it)
            // canvas.drawBitmap(bitmap, 0f, textHeight, paint)
            imageHeight = bitmap.height
            imageWidth = bitmap.width
            val bitmapDrawable = BitmapDrawable(resources, bitmap)
            bitmapDrawable.setBounds(
                paddingLeft, (paddingTop + textHeight).toInt(),
                paddingLeft + bitmap.width, paddingTop + bitmap.height
            )
            bitmapDrawable.draw(canvas)
        }
    }

    private fun drawCircle(canvas: Canvas, contentWidth: Int, contentHeight: Int) {
        val cx = paddingLeft + sampleRadius / 2
        val cy = paddingTop + textHeight + imageHeight + sampleRadius / 2
        paint.setColor(Color.RED)
        paint.strokeWidth = 10f
        canvas.drawCircle(cx, cy, sampleRadius, paint)

        // 给canvas画布上色
        canvas.drawARGB(125, 255, 23, 26)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 计算view的内容宽高
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        drawText(canvas, contentWidth, contentHeight)
        drawImage(canvas, contentWidth, contentHeight)
        drawCircle(canvas, contentWidth, contentHeight)
    }
}