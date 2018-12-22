package com.uroad.library.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.uroad.library.R

class CircularProgressView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    private var angleAnimatorDuration: Int = 0
    private var sweepAnimatorDuration: Int = 0
    private var minSweepAngle: Int = 0
    private var mBorderWidth: Float = 0F
    private val fBounds = RectF()
    private var mObjectAnimatorSweep: ObjectAnimator
    private var mObjectAnimatorAngle: ObjectAnimator
    private var mModeAppearing = true
    private var mPaint: Paint
    private var mCurrentGlobalAngleOffset: Float = 0.toFloat()
    private var mCurrentGlobalAngle: Float = 0.toFloat()
    private var mCurrentSweepAngle: Float = 0.toFloat()
    private var mRunning: Boolean = false
    private var mColors: IntArray
    private var mCurrentColorIndex: Int = 0
    private var mNextColorIndex: Int = 0

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val mAngleProperty = object : Property<CircularProgressView, Float>(Float::class.java, "angle") {
        override fun get(view: CircularProgressView): Float {
            return view.getCurrentGlobalAngle()
        }

        override fun set(view: CircularProgressView, value: Float) {
            view.setCurrentGlobalAngle(value)
        }
    }

    private val mSweepProperty = object : Property<CircularProgressView, Float>(Float::class.java, "arc") {
        override fun get(view: CircularProgressView): Float {
            return view.getCurrentSweepAngle()
        }

        override fun set(view: CircularProgressView, value: Float) {
            view.setCurrentSweepAngle(value)
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView, defStyleAttr, 0)
        mBorderWidth = a.getDimension(R.styleable.CircularProgressView_border_Width, resources.getDimension(R.dimen.space_4))
        angleAnimatorDuration = a.getInt(R.styleable.CircularProgressView_angleAnimationDurationMillis,
                resources.getInteger(R.integer.circular_default_angleAnimationDurationMillis))
        sweepAnimatorDuration = a.getInt(R.styleable.CircularProgressView_sweepAnimationDurationMillis,
                resources.getInteger(R.integer.circular_default_sweepAnimationDuration))
        minSweepAngle = a.getInt(R.styleable.CircularProgressView_minSweepAngle,
                resources.getInteger(R.integer.circular_default_miniSweepAngle))
        val colorArrayId = a.getResourceId(R.styleable.CircularProgressView_colorSequence, R.array.circular_loading_color_sequence)
        mColors = resources.getIntArray(colorArrayId)
        a.recycle()
        mCurrentColorIndex = 0
        mNextColorIndex = 1
        mPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mBorderWidth
            color = mColors[mCurrentColorIndex]
        }
        mObjectAnimatorAngle = ObjectAnimator.ofFloat<CircularProgressView>(this, mAngleProperty, 360f).apply {
            interpolator = LinearInterpolator()
            duration = angleAnimatorDuration.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }
        mObjectAnimatorSweep = ObjectAnimator.ofFloat<CircularProgressView>(this, mSweepProperty, 360f - minSweepAngle * 2).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = sweepAnimatorDuration.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {

                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {
                    toggleAppearingMode()
                }
            })
        }
    }

    private fun innerStart() {
        if (isRunning()) {
            return
        }
        mRunning = true
        mObjectAnimatorAngle.start()
        mObjectAnimatorSweep.start()
        invalidate()
    }

    private fun innerStop() {
        if (!isRunning()) {
            return
        }
        mRunning = false
        mObjectAnimatorAngle.cancel()
        mObjectAnimatorSweep.cancel()
        invalidate()
    }

    private fun isRunning(): Boolean {
        return mRunning
    }

    override fun onVisibilityChanged(view: View, visibility: Int) {
        super.onVisibilityChanged(view, visibility)
        if (visibility == View.VISIBLE) {
            innerStart()
        } else {
            innerStop()
        }
    }

    override fun onAttachedToWindow() {
        innerStart()
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        innerStop()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fBounds.left = mBorderWidth / 2f + .5f
        fBounds.right = w.toFloat() - mBorderWidth / 2f - .5f
        fBounds.top = mBorderWidth / 2f + .5f
        fBounds.bottom = h.toFloat() - mBorderWidth / 2f - .5f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
        var sweepAngle = mCurrentSweepAngle
        if (mModeAppearing) {
            mPaint.color = gradient(mColors[mCurrentColorIndex], mColors[mNextColorIndex], mCurrentSweepAngle / (360 - minSweepAngle * 2))
            sweepAngle += minSweepAngle.toFloat()
        } else {
            startAngle += sweepAngle
            sweepAngle = 360f - sweepAngle - minSweepAngle.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint)
    }

    private fun gradient(color1: Int, color2: Int, p: Float): Int {
        val r1 = color1 and 0xff0000 shr 16
        val g1 = color1 and 0xff00 shr 8
        val b1 = color1 and 0xff
        val r2 = color2 and 0xff0000 shr 16
        val g2 = color2 and 0xff00 shr 8
        val b2 = color2 and 0xff
        val newr = (r2 * p + r1 * (1 - p)).toInt()
        val newg = (g2 * p + g1 * (1 - p)).toInt()
        val newb = (b2 * p + b1 * (1 - p)).toInt()
        return Color.argb(255, newr, newg, newb)
    }

    private fun toggleAppearingMode() {
        mModeAppearing = !mModeAppearing
        if (mModeAppearing) {
            mCurrentColorIndex = ++mCurrentColorIndex % mColors.size
            mNextColorIndex = ++mNextColorIndex % mColors.size
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + minSweepAngle * 2) % 360
        }
    }

    fun setCurrentGlobalAngle(currentGlobalAngle: Float) {
        mCurrentGlobalAngle = currentGlobalAngle
        invalidate()
    }

    fun getCurrentGlobalAngle(): Float {
        return mCurrentGlobalAngle
    }

    fun setCurrentSweepAngle(currentSweepAngle: Float) {
        mCurrentSweepAngle = currentSweepAngle
        invalidate()
    }

    fun getCurrentSweepAngle(): Float {
        return mCurrentSweepAngle
    }
}