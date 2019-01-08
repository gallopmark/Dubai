package com.uroad.dubai.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.uroad.dubai.R
import com.uroad.library.utils.DisplayUtils

class RadarWaveView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /**
     * 振幅
     */
    private var amplitude: Int = 0
    /**
     * 偏距
     */
    private var offset: Int = 0

    /**
     * 波形的颜色
     */
    private var waveColor = -0x550081c9

    /**
     * 初相
     */
    private var phase: Float = 0.toFloat()

    /**
     * 波形移动的速度
     */
    private var waveSpeed = 3f

    /**
     * 角速度
     */
    private var angularSpeed: Double = 0.toDouble()

    /**
     * 开始位置相差多少个周期
     */
    private var startPeriod: Double = 0.toDouble()

    /**
     * 是否直接开启波形
     */
    private var waveStart: Boolean = false

    private lateinit var path: Path
    private var paint: Paint? = null

    private val sin = 0
    private val cos = 1

    private var waveType: Int = 0

    private val mTop = 0
    private val mBottom = 1

    private var waveFillType: Int = 0

    private var valueAnimator: ValueAnimator? = null

    init {
        getAttr(context, attrs)
        offset = amplitude
        initPaint()
        initAnimation()
    }

    private fun getAttr(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarWaveView)
        waveType = typedArray.getInt(R.styleable.RadarWaveView_waveType, sin)
        waveFillType = typedArray.getInt(R.styleable.RadarWaveView_waveFillType, mBottom)
        amplitude = typedArray.getDimensionPixelOffset(R.styleable.RadarWaveView_waveAmplitude, DisplayUtils.dip2px(context, 10f))
        waveColor = typedArray.getColor(R.styleable.RadarWaveView_waveColor, waveColor)
        waveSpeed = typedArray.getFloat(R.styleable.RadarWaveView_waveSpeed, waveSpeed)
        startPeriod = typedArray.getFloat(R.styleable.RadarWaveView_waveStartPeriod, 0f).toDouble()
        waveStart = typedArray.getBoolean(R.styleable.RadarWaveView_waveStart, false)
        typedArray.recycle()
    }

    private fun initPaint() {
        path = Path()
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            color = waveColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        angularSpeed = 2 * Math.PI / width
    }

    override fun onDraw(canvas: Canvas) {
        when (waveType) {
            sin -> drawSin(canvas)
            cos -> drawCos(canvas)
        }
    }


    /**
     * 根据cos函数绘制波形
     *
     * @param canvas
     */
    private fun drawCos(canvas: Canvas) {

        when (waveFillType) {
            mTop -> fillTop(canvas)
            mBottom -> fillBottom(canvas)
        }
    }

    /**
     * 根据sin函数绘制波形
     *
     * @param canvas
     */
    private fun drawSin(canvas: Canvas) {
        when (waveFillType) {
            mTop -> fillTop(canvas)
            mBottom -> fillBottom(canvas)
        }
    }

    /**
     * 填充波浪上面部分
     */
    private fun fillTop(canvas: Canvas) {
        phase -= waveSpeed / 100
        var y: Float
        path.reset()
        path.moveTo(0f, height.toFloat())
        var x = 0f
        while (x <= width) {
            y = (amplitude * Math.sin(angularSpeed * x + phase.toDouble() + Math.PI * startPeriod) + offset).toFloat()
            path.lineTo(x, height - y)
            x += 20f
        }
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(0f, 0f)
        path.close()
        canvas.drawPath(path, paint)
    }

    /**
     * 填充波浪下面部分
     */
    private fun fillBottom(canvas: Canvas) {
        phase -= waveSpeed / 100
        var y: Float
        path.reset()
        path.moveTo(0f, 0f)
        var x = 0f
        while (x <= width) {
            y = (amplitude * Math.sin(angularSpeed * x + phase.toDouble() + Math.PI * startPeriod) + offset).toFloat()
            path.lineTo(x, y)
            x += 20f
        }
        //填充矩形
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, width).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                /**
                 * 刷新页面调取onDraw方法，通过变更φ 达到移动效果
                 */
                /**
                 * 刷新页面调取onDraw方法，通过变更φ 达到移动效果
                 */
                invalidate()
            }
            if (waveStart) {
                this.start()
            }
        }
    }

    fun startAnimation() {
        valueAnimator?.start()
    }

    fun stopAnimation() {
        valueAnimator?.cancel()
    }

    fun pauseAnimation() {
        valueAnimator?.pause()
    }

    fun resumeAnimation() {
        valueAnimator?.resume()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }
}