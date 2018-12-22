package com.uroad.library.banner

import android.support.v7.widget.RecyclerView

class AnimManager {
    companion object {
        private const val ANIM_BOTTOM_TO_TOP = 0
        private const val ANIM_TOP_TO_BOTTOM = 1
    }

    /**
     * 动画类型
     */
    private var mAnimType = ANIM_BOTTOM_TO_TOP
    /**
     * 变化因子
     */
    private var mAnimFactor = 0.2f

    fun setAnimation(recyclerView: RecyclerView, position: Int, percent: Float) {
        when (mAnimType) {
            ANIM_BOTTOM_TO_TOP -> setBottomToTopAnim(recyclerView, position, percent)
            ANIM_TOP_TO_BOTTOM -> setTopToBottomAnim(recyclerView, position, percent)
            else -> setBottomToTopAnim(recyclerView, position, percent)
        }
    }


    /**
     * 从下到上的动画效果
     *
     * @param recyclerView RecyclerView
     * @param position     int
     * @param percent      float
     */
    private fun setBottomToTopAnim(recyclerView: RecyclerView, position: Int, percent: Float) {
        val layoutManager = recyclerView.layoutManager ?: return
        // 中间页
        val mCurView = layoutManager.findViewByPosition(position)
        // 右边页
        val mRightView = layoutManager.findViewByPosition(position + 1)
        // 左边页
        val mLeftView = layoutManager.findViewByPosition(position - 1)
        // 右右边页
        val mRRView = layoutManager.findViewByPosition(position + 2)
        mLeftView?.let {
            it.scaleX = 1 - mAnimFactor + percent * mAnimFactor
            it.scaleY = 1 - mAnimFactor + percent * mAnimFactor
        }
        mCurView?.let {
            it.scaleX = 1 - percent * mAnimFactor
            it.scaleY = 1 - percent * mAnimFactor
        }
        mRightView?.let {
            it.scaleX = 1 - mAnimFactor + percent * mAnimFactor
            it.scaleY = 1 - mAnimFactor + percent * mAnimFactor
        }
        mRRView?.let {
            it.scaleX = 1 - percent * mAnimFactor
            it.scaleY = 1 - percent * mAnimFactor
        }
    }


    /***
     * 从上到下的效果
     * @param recyclerView RecyclerView
     * @param position int
     * @param percent int
     */
    private fun setTopToBottomAnim(recyclerView: RecyclerView, position: Int, percent: Float) {
        val layoutManager = recyclerView.layoutManager ?: return
        // 中间页
        val mCurView = layoutManager.findViewByPosition(position)
        // 右边页
        val mRightView = layoutManager.findViewByPosition(position + 1)
        // 左边页
        val mLeftView = layoutManager.findViewByPosition(position - 1)
        // 左左边页
        val mLLView = layoutManager.findViewByPosition(position - 2)
        mLeftView?.let {
            it.scaleX = 1 - percent * mAnimFactor
            it.scaleY = 1 - percent * mAnimFactor
        }
        mCurView?.let {
            it.scaleX = 1 - mAnimFactor + percent * mAnimFactor
            it.scaleY = 1 - mAnimFactor + percent * mAnimFactor
        }
        mRightView?.let {
            it.scaleX = 1 - percent * mAnimFactor
            it.scaleY = 1 - percent * mAnimFactor
        }
        mLLView?.let {
            it.scaleX = 1 - mAnimFactor + percent * mAnimFactor
            it.scaleY = 1 - mAnimFactor + percent * mAnimFactor
        }
    }

    fun setAnimFactor(mAnimFactor: Float) {
        this.mAnimFactor = mAnimFactor
    }

    fun setAnimType(mAnimType: Int) {
        this.mAnimType = mAnimType
    }
}
