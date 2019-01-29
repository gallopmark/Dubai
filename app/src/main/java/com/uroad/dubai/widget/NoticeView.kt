package com.uroad.dubai.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import com.uroad.dubai.R

class NoticeView : FrameLayout {

    companion object {
        /**  动画间隔   */
        private var ANIM_DELAYED_MILLIONS = 3 * 1000
        /**  动画持续时长   */
        private var ANIM_DURATION = 1000
        /**  默认字体颜色   */
        private var DEFAULT_TEXT_COLOR: Int = 0
        /**  默认字体大小  dp   */
        private var DEFAULT_TEXT_SIZE: Float = 0f
        private var MAX_LINE = 2
    }

    private lateinit var mAnimOut: Animation
    private lateinit var mAnimIn: Animation
    private lateinit var mTipOutTv: TextView
    private lateinit var mTipIntTv: TextView
    /**  循环播放的消息   */
    private var tipList: MutableList<String?>? = null
    /**  当前轮播到的消息索引   */
    private var curTipIndex = 0
    private var lastTimeMillis: Long = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
        initTipFrame()
        initAnimation()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NoticeView)
        ANIM_DELAYED_MILLIONS = ta.getInteger(R.styleable.NoticeView_anim_delayed, ANIM_DELAYED_MILLIONS)
        ANIM_DURATION = ta.getInteger(R.styleable.NoticeView_anim_duration, ANIM_DURATION)
        DEFAULT_TEXT_COLOR = ta.getColor(R.styleable.NoticeView_tipTextColor, ContextCompat.getColor(context, R.color.appTextColor))
        DEFAULT_TEXT_SIZE = ta.getDimension(R.styleable.NoticeView_tipTextSizs, context.resources.getDimension(R.dimen.font_14))
        MAX_LINE = ta.getInteger(R.styleable.NoticeView_tipMaxLine, MAX_LINE)
        ta.recycle()
    }

    private fun initTipFrame() {
        mTipOutTv = newTextView()
        mTipIntTv = newTextView()
        addView(mTipIntTv)
        addView(mTipOutTv)
    }

    /**
     * 设置要循环播放的信息
     */
    fun setTipList(tipList: MutableList<String?>) {
        release()
        this.tipList = tipList
        curTipIndex = 0
        updateTip(mTipOutTv)
        updateTipAndPlayAnimation()
    }

    private fun initAnimation() {
        mAnimOut = newAnimation(0f, -1f)
        mAnimIn = newAnimation(1f, 0f)
        mAnimIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                updateTipAndPlayAnimationWithCheck()
            }
        })
    }

    private fun updateTipAndPlayAnimation() {
        if (curTipIndex % 2 == 0) {
            updateTip(mTipOutTv)
            mTipIntTv.startAnimation(mAnimOut)
            mTipOutTv.startAnimation(mAnimIn)
            this.bringChildToFront(mTipIntTv)
        } else {
            updateTip(mTipIntTv)
            mTipOutTv.startAnimation(mAnimOut)
            mTipIntTv.startAnimation(mAnimIn)
            this.bringChildToFront(mTipOutTv)
        }
    }

    private fun updateTipAndPlayAnimationWithCheck() {
        if (System.currentTimeMillis() - lastTimeMillis < 1000) {
            return
        }
        lastTimeMillis = System.currentTimeMillis()
        updateTipAndPlayAnimation()
    }

    private fun updateTip(tipView: TextView) {
        tipView.text= getNextTip()
    }

    /**
     * 获取下一条消息
     */
    private fun getNextTip(): String? {
        val list = this.tipList
        return if (list.isNullOrEmpty()) null
        else list[curTipIndex++ % list.size]
    }

    private fun newTextView(): TextView {
        val textView = TextView(context)
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        textView.layoutParams = lp
        textView.gravity = Gravity.CENTER or Gravity.START
        textView.setLines(MAX_LINE)
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.setTextColor(DEFAULT_TEXT_COLOR)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE)
        return textView
    }

    private fun newAnimation(fromYValue: Float, toYValue: Float): Animation {
        val anim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, fromYValue, Animation.RELATIVE_TO_SELF, toYValue)
        anim.duration = ANIM_DURATION.toLong()
        anim.startOffset = ANIM_DELAYED_MILLIONS.toLong()
        anim.interpolator = DecelerateInterpolator()
        return anim
    }

    fun release() {
        tipList?.clear()
        mTipIntTv.clearAnimation()
        mTipOutTv.clearAnimation()
    }
}