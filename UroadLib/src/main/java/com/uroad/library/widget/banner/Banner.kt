package com.uroad.library.widget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.AttrRes
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.Scroller
import com.uroad.library.R
import com.uroad.library.utils.DisplayUtils

/**
 * 自定义无限轮转的海报控件，抽离了适配器和滑动事件，更加轻便。
 * 请在布局使用当前类，相关属性可以通过布局或者代码设置，
 * 适配器继承BannerBaseAdapter，指定数据类型，填充布局即可
 */
class Banner : FrameLayout {

    private var mContext: Context
    private lateinit var mRootView: View
    private lateinit var mViewPager: ViewPager

    // 页面边距
    private var pageMargin = 15f
    // 页面显示屏幕占比
    private var pagePercent = 0.8f
    // 缩放和透明比例，需要自己修改想要的比例
    private var scaleMin = 0.8f
    private var alphaMin = 0.8f

    // 自动轮播间隔时长
    private var mScrollDuration: Long = 4000
    private var mAnimDuration: Long = 1200
    // 是否是动画滚动
    private var isAnimScroll: Boolean = false
    private var isAutoScroll: Boolean = false

    private val mHandler = Handler(Looper.getMainLooper())
    private var mScrollTask: AutoScrollTask? = null
    private var mRecentTouchTime: Long = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initAttrs(attrs)
        initView(context)
        initEvent()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val dm = mContext.resources.displayMetrics
        pageMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pageMargin, dm)
        val a = mContext.obtainStyledAttributes(attrs, R.styleable.Banner)
        pageMargin = a.getDimension(R.styleable.Banner_bannerPageMargin, pageMargin)
        pagePercent = a.getFloat(R.styleable.Banner_bannerPagePercent, pagePercent)
        scaleMin = a.getFloat(R.styleable.Banner_bannerPageScale, scaleMin)
        alphaMin = a.getFloat(R.styleable.Banner_bannerPageAlpha, alphaMin)
        mScrollDuration = a.getInteger(R.styleable.Banner_bannerScrollDuration, mScrollDuration.toInt()).toLong()
        mAnimDuration = a.getInteger(R.styleable.Banner_bannerAnimDuration, mAnimDuration.toInt()).toLong()
        isAnimScroll = a.getBoolean(R.styleable.Banner_bannerAnimScroll, isAnimScroll)
        isAutoScroll = a.getBoolean(R.styleable.Banner_bannerAutoScroll, isAutoScroll)
        a.recycle()
    }

    private fun initView(context: Context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.banner_view, this)
        mViewPager = mRootView.findViewById(R.id.viewPager)
        // 注意clipChildren属性的使用
        // 初始化ViewPager
        val params = mViewPager.layoutParams as FrameLayout.LayoutParams
        params.width = (DisplayUtils.getWindowWidth(context) * pagePercent).toInt()
        params.gravity = Gravity.CENTER
        mViewPager.layoutParams = params
        mViewPager.pageMargin = pageMargin.toInt()
        mViewPager.setPageTransformer(false, BannerPageTransformer())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        mViewPager.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    stopAutoScroll()
                }
                MotionEvent.ACTION_UP -> {
                    if (isAutoScroll) startAutoScroll()
                }
            }
            false
        }
        // 父亲将触摸事件交给孩子处理
        mRootView.findViewById<View>(R.id.viewPager_container).setOnTouchListener { view, motionEvent -> mViewPager.dispatchTouchEvent(motionEvent) }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 视图初始化完毕，开始轮播任务
        if (mScrollTask == null) mScrollTask = AutoScrollTask()
        if (isAutoScroll) startAutoScroll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScrollTask?.stop()
    }

    /**
     * 自动轮播任务
     */
    private inner class AutoScrollTask : Runnable {

        override fun run() {
            val currentPosition = mViewPager.currentItem
            mViewPager.adapter?.let {
                if (currentPosition == it.count - 1) {  // 最后一页
                    mViewPager.currentItem = 0
                } else {
                    mViewPager.currentItem = currentPosition + 1
                }
            }
            // 一直给自己发消息
            mHandler.postDelayed(this, mScrollDuration)
        }

        fun start() {
            mHandler.removeCallbacks(this)
            mHandler.postDelayed(this, mScrollDuration)
        }

        fun stop() {
            mHandler.removeCallbacks(this)
        }
    }

    /**
     * 设置滑动动画持续时间
     */
    fun setAnimationScroll(during: Int) {
        try {
            // viewPager平移动画事件
            val mField = ViewPager::class.java.getDeclaredField("mScroller")
            mField.isAccessible = true
            val mScroller = object : Scroller(context,
                    // 动画效果与ViewPager的一致
                    Interpolator { t ->
                        var dt = t
                        dt -= 1.0f
                        dt * dt * dt * dt * dt + 1.0f
                    }) {
                override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
                    var d = duration
                    // 如果手动滚动,则加速滚动
                    if (System.currentTimeMillis() - mRecentTouchTime > mScrollDuration && isAnimScroll) {
                        d = during    // 动画滑动
                    } else {
                        d /= 2   // 手势滚动
                    }
                    super.startScroll(startX, startY, dx, dy, d)
                }

                override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
                    super.startScroll(startX, startY, dx, dy, during)
                }
            }
            mField.set(mViewPager, mScroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mRecentTouchTime = System.currentTimeMillis()
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * ViewPager Item动画转换类
     */
    private inner class BannerPageTransformer : ViewPager.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            //            // 不同位置的缩放和透明度
            val scale = if (position < 0) (1 - scaleMin) * position + 1
            else (scaleMin - 1) * position + 1
            val alpha = if (position < 0) (1 - alphaMin) * position + 1
            else (alphaMin - 1) * position + 1
            // 保持左右两边的图片位置中心
            if (position < 0) {
                page.pivotX = page.width.toFloat()
                page.pivotY = (page.height / 2).toFloat()
            } else {
                page.pivotX = 0f
                page.pivotY = (page.height / 2).toFloat()
            }
            page.scaleX = scale
            page.scaleY = scale
            page.alpha = Math.abs(alpha)
        }
    }

    /**
     * ==================================API==================================
     */
    fun getViewPager(): ViewPager {
        return mViewPager
    }

    fun setAdapter(adapter: PagerAdapter) {
        mViewPager.adapter = adapter
    }

    /**
     * 开启自动轮播
     */
    fun startAutoScroll() {
        if (mScrollTask == null) return
        mScrollTask?.start()
        setAnimationScroll(mAnimDuration.toInt())
    }

    /**
     * 停止自动轮播
     */
    fun stopAutoScroll() {
        if (mScrollTask == null) return
        mScrollTask?.stop()
    }

    /**
     * 是否动画轮播
     */
    fun isAnimScroll(isAnimScroll: Boolean) {
        this.isAnimScroll = isAnimScroll
    }

    /**
     * 重置当前的位置
     */
    fun resetCurrentPosition(size: Int) {
        if (size == 0) return
        // 去除动画
        // isAnimScroll(false);
        mViewPager.currentItem = size * 1000
    }

    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mViewPager.addOnPageChangeListener(listener)
    }

    fun setPageMargin(pageMargin: Float) {
        this.pageMargin = pageMargin
    }

    fun setPagePercent(pagePercent: Float) {
        this.pagePercent = pagePercent
    }

    fun setPageScale(scale: Float) {
        this.scaleMin = scale
    }

    fun setPageAlpha(alpha: Float) {
        this.alphaMin = alpha
    }

    fun setScrollDuration(scrollDuration: Long) {
        mScrollDuration = scrollDuration
    }

    fun setAnimDuration(animDuration: Long) {
        mAnimDuration = animDuration
    }
}