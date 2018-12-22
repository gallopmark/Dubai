package com.uroad.library.banner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.v7.widget.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.uroad.library.R
import com.uroad.library.utils.DisplayUtils

class GalleryRecyclerView : RecyclerView, View.OnTouchListener, GalleryItemDecoration.OnItemSizeMeasuredListener {
    companion object {
        const val LINEAR_SNAP_HELPER = 0
        const val PAGER_SNAP_HELPER = 1
    }

    /**
     * 滑动速度
     */
    private var mFlingSpeed = 1000
    /**
     * 是否自动播放
     */
    private var mAutoPlay = false
    /**
     * 自动播放间隔时间
     */
    private var mInterval = 1000
    private var mInitPos = -1

    private var mPosition = 0

    /**
     * x方向消耗距离，使偏移量为左边距 + 左边Item的可视部分宽度
     */
    private var mConsumeX = 0
    private var mConsumeY = 0

    private val mAnimManager: AnimManager
    private lateinit var mDecoration: GalleryItemDecoration
    private var onPageChangedListener: OnPageChangedListener? = null
    private lateinit var mHandler: Handler

    private val mAutoPlayTask = object : Runnable {
        override fun run() {
            val adapter = adapter
            if (adapter == null || adapter.itemCount <= 0) {
                return
            }
            val position = getScrolledPosition()
            val itemCount = adapter.itemCount
            val newPosition = (position + 1) % itemCount
            smoothScrollToPosition(newPosition)
            mHandler.removeCallbacks(this)
            mHandler.postDelayed(this, mInterval.toLong())
        }
    }

    private fun getDecoration(): GalleryItemDecoration {
        return mDecoration
    }

    private fun getAnimManager(): AnimManager {
        return mAnimManager
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GalleryRecyclerView)
        val helper = ta.getInteger(R.styleable.GalleryRecyclerView_helper, LINEAR_SNAP_HELPER)
        mAutoPlay = ta.getBoolean(R.styleable.GalleryRecyclerView_autoPlay, false)
        ta.recycle()
        mAnimManager = AnimManager()
        attachDecoration()
        attachToRecyclerHelper(helper)
        //设置触碰监听
        setOnTouchListener(this)
        mHandler = Handler(Looper.getMainLooper())
    }

    private fun attachDecoration() {
        mDecoration = GalleryItemDecoration().apply { setOnItemSizeMeasuredListener(this@GalleryRecyclerView) }
        addItemDecoration(mDecoration)
    }

    private fun attachToRecyclerHelper(helper: Int) {
        initScrollListener()
        initSnapHelper(helper)
    }

    private fun initScrollListener() {
        val mScrollerListener = GalleryScrollerListener()
        addOnScrollListener(mScrollerListener)
    }

    private fun initSnapHelper(helper: Int) {
        when (helper) {
            GalleryRecyclerView.LINEAR_SNAP_HELPER -> {
                val mLinearSnapHelper = LinearSnapHelper()
                mLinearSnapHelper.attachToRecyclerView(this)
            }
            GalleryRecyclerView.PAGER_SNAP_HELPER -> {
                val mPagerSnapHelper = PagerSnapHelper()
                mPagerSnapHelper.attachToRecyclerView(this)
            }
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling(balanceVelocity(velocityX), balanceVelocity(velocityY))
    }

    /**
     * 返回滑动速度值
     */
    private fun balanceVelocity(velocity: Int): Int {
        return if (velocity > 0) {
            Math.min(velocity, mFlingSpeed)
        } else {
            Math.max(velocity, -mFlingSpeed)
        }
    }

    /**
     * 设置页面参数，单位dp
     *
     * @param pageMargin           默认：0dp
     * @param leftPageVisibleWidth 默认：50dp
     * @return GalleryRecyclerView
     */
    fun initPageParams(pageMargin: Int, leftPageVisibleWidth: Int): GalleryRecyclerView {
        mDecoration.mPageMargin = pageMargin
        mDecoration.mLeftPageVisibleWidth = leftPageVisibleWidth
        return this
    }

    /**
     * 设置滑动速度（像素/s）
     *
     * @param speed int
     * @return GalleryRecyclerView
     */
    fun initFlingSpeed(@IntRange(from = 0) speed: Int): GalleryRecyclerView {
        this.mFlingSpeed = speed
        return this
    }

    /**
     * 设置动画因子
     *
     * @param factor float
     * @return GalleryRecyclerView
     */
    fun setAnimFactor(@FloatRange(from = 0.0) factor: Float): GalleryRecyclerView {
        mAnimManager.setAnimFactor(factor)
        return this
    }

    /**
     * 设置动画类型
     *
     * @param type int
     * @return GalleryRecyclerView
     */
    fun setAnimType(type: Int): GalleryRecyclerView {
        mAnimManager.setAnimType(type)
        return this
    }

    /**
     * 设置点击事件
     *
     * @param mListener OnItemClickListener
     */
    fun setOnItemClickListener(mListener: OnItemClickListener): GalleryRecyclerView {
        mDecoration.setOnItemClickListener(mListener)
        return this
    }

    fun setOnPageChangedListener(onPageChangedListener: OnPageChangedListener): GalleryRecyclerView {
        this.onPageChangedListener = onPageChangedListener
        return this
    }

    /**
     * 是否自动滚动
     *
     * @param auto boolean
     * @return GalleryRecyclerView
     */
    fun autoPlay(auto: Boolean): GalleryRecyclerView {
        this.mAutoPlay = auto
        return this
    }

    /**
     * 自动播放
     */
    private fun autoPlayGallery() {
        if (mAutoPlay) {
            mHandler.removeCallbacks(mAutoPlayTask)
            mHandler.postDelayed(mAutoPlayTask, mInterval.toLong())
        }
    }

    /**
     * 移除自动播放Runnable
     */
    private fun removeAutoPlayTask() {
        if (mAutoPlay) {
            mHandler.removeCallbacks(mAutoPlayTask)
        }
    }

    /**
     * 装载
     *
     * @return GalleryRecyclerView
     */
    fun setUp(): GalleryRecyclerView {
        val adapter = adapter ?: return this
        if (adapter.itemCount <= 0) {
            return this
        }
        smoothScrollToPosition(0)
        updateConsume()
        autoPlayGallery()
        return this
    }

    /**
     * 释放资源
     */
    fun release() {
        removeAutoPlayTask()
    }

    fun getOrientation(): Int {
        return if (layoutManager is LinearLayoutManager) {
            if (layoutManager is GridLayoutManager) {
                throw RuntimeException("请设置LayoutManager为LinearLayoutManager")
            } else {
                (layoutManager as LinearLayoutManager).orientation
            }
        } else {
            throw RuntimeException("请设置LayoutManager为LinearLayoutManager")
        }
    }

    fun getScrolledPosition(): Int {
        return mPosition
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        // 如果是横竖屏切换（Fragment销毁），不应该走smoothScrollToPosition(0)，因为这个方法会导致ScrollManager的onHorizontalScroll不断执行，而ScrollManager.mConsumeX已经重置，会导致这个值紊乱
        // 而如果走scrollToPosition(0)方法，则不会导致ScrollManager的onHorizontalScroll执行，所以ScrollManager.mConsumeX这个值不会错误
        scrollToPosition(0)
        // 但是因为不走ScrollManager的onHorizontalScroll，所以不会执行切换动画，所以就调用smoothScrollBy(int dx, int dy)，让item轻微滑动，触发动画
        smoothScrollBy(10, 0)
        smoothScrollBy(0, 0)
        autoPlayGallery()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> removeAutoPlayTask()
            MotionEvent.ACTION_MOVE -> removeAutoPlayTask()
            MotionEvent.ACTION_UP -> autoPlayGallery()
            else -> {
            }
        }
        return false
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

    /**
     * 播放间隔时间 ms
     *
     * @param interval int
     * @return GalleryRecyclerView
     */
    fun intervalTime(@IntRange(from = 10) interval: Int): GalleryRecyclerView {
        this.mInterval = interval
        return this
    }

    /**
     * 开始处于的位置
     *
     * @param i int
     * @return GalleryRecyclerView
     */
    fun initPosition(@IntRange(from = 0) i: Int): GalleryRecyclerView {
        var position = i
        val adapter = adapter ?: return this
        if (position >= adapter.itemCount) {
            position = adapter.itemCount - 1
        } else if (position < 0) {
            position = 0
        }
        mInitPos = position
        return this
    }

    override fun onItemSizeMeasured(size: Int) {
        if (mInitPos < 0) {
            return
        }
        if (mInitPos == 0) {
            scrollToPosition(0)
        } else {
            if (getOrientation() == LinearLayoutManager.HORIZONTAL) {
                smoothScrollBy(mInitPos * size, 0)
            } else {
                smoothScrollBy(0, mInitPos * size)
            }
        }
        mInitPos = -1
    }

    private inner class GalleryScrollerListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (getOrientation() == LinearLayoutManager.HORIZONTAL) {
                onHorizontalScroll(recyclerView, dx)
            } else {
                onVerticalScroll(recyclerView, dy)
            }
            val layoutManager = layoutManager as LinearLayoutManager?
            if (layoutManager != null && onPageChangedListener != null && layoutManager.findFirstCompletelyVisibleItemPosition() >= 0) {
                onPageChangedListener?.onPageChange(layoutManager.findLastCompletelyVisibleItemPosition())
            }
        }
    }

    private fun updateConsume() {
        mConsumeX += DisplayUtils.dip2px(context, (getDecoration().mLeftPageVisibleWidth + getDecoration().mPageMargin * 2).toFloat())
        mConsumeY += DisplayUtils.dip2px(context, (getDecoration().mLeftPageVisibleWidth + getDecoration().mPageMargin * 2).toFloat())
    }

    /**
     * 垂直滑动
     *
     * @param recyclerView RecyclerView
     * @param dy           int
     */
    private fun onVerticalScroll(recyclerView: RecyclerView, dy: Int) {
        mConsumeY += dy
        // 让RecyclerView测绘完成后再调用，避免GalleryAdapterHelper.mItemHeight的值拿不到
        recyclerView.post {
            val shouldConsumeY = getDecoration().mItemConsumeY
            // 位置浮点值（即总消耗距离 / 每一页理论消耗距离 = 一个浮点型的位置值）
            val offset = mConsumeY.toFloat() / shouldConsumeY.toFloat()
            // 获取当前页移动的百分值
            val percent = offset - offset.toInt()
            mPosition = offset.toInt()
            // 设置动画变化
            getAnimManager().setAnimation(recyclerView, mPosition, percent)
        }
    }

    /**
     * 水平滑动
     *
     * @param recyclerView RecyclerView
     * @param dx           int
     */
    private fun onHorizontalScroll(recyclerView: RecyclerView, dx: Int) {
        mConsumeX += dx
        // 让RecyclerView测绘完成后再调用，避免GalleryAdapterHelper.mItemWidth的值拿不到
        recyclerView.post {
            val shouldConsumeX = getDecoration().mItemConsumeX
            // 位置浮点值（即总消耗距离 / 每一页理论消耗距离 = 一个浮点型的位置值）
            val offset = mConsumeX.toFloat() / shouldConsumeX.toFloat()
            // 获取当前页移动的百分值
            val percent = offset - offset.toInt()
            mPosition = offset.toInt()
            // 设置动画变化
            getAnimManager().setAnimation(recyclerView, mPosition, percent)
        }
    }

    fun getPosition(): Int {
        return mPosition
    }

    interface OnPageChangedListener {
        fun onPageChange(position: Int)
    }
}