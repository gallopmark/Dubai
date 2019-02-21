package com.uroad.dubai.widget

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.uroad.library.utils.DisplayUtils


class AppbarLayoutZoomBehavior(context: Context, attributeSet: AttributeSet?)
    : AppBarLayout.Behavior(context, attributeSet) {
    private var mZoomView: View? = null
    private var mAppbarHeight: Int = 0//记录AppbarLayout原始高度
    private var mImageViewHeight: Int = 0//记录ImageView原始高度
    private var maxZoomHeight = DisplayUtils.getWindowHeight(context).toFloat() //放大最大高度

    private var mTotalDy: Float = 0f//手指在Y轴滑动的总距离
    private var mScaleValue: Float = 0f//图片缩放比例
    private var mLastBottom: Int = 0//Appbar的变化高度
    private var valueAnimator: ValueAnimator? = null

    private var isAnimate: Boolean = false//是否做动画标志
    override fun onLayoutChild(parent: CoordinatorLayout, abl: AppBarLayout, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, abl, layoutDirection)
        init(abl)
        return handled
    }

    /**
     * 进行初始化操作，在这里获取到ImageView的引用，和Appbar的原始高度
     */
    private fun init(abl: AppBarLayout) {
        abl.clipChildren = false
        mAppbarHeight = abl.height
        mZoomView = abl.findViewById(com.uroad.dubai.R.id.appbarZoomView)
        mZoomView?.let { mImageViewHeight = it.height }
    }

    fun setMaxZoomHeight(maxZoomHeight: Float) {
        this.maxZoomHeight = maxZoomHeight
    }

    /**
     * 是否处理嵌套滑动
     */
    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        isAnimate = true
        return true
    }

    /**
     * 在这里做具体的滑动处理
     */
    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mZoomView != null && child.bottom >= mAppbarHeight && dy < 0 && type == ViewCompat.TYPE_TOUCH) {
            zoomHeaderImageView(child, dy)
        } else {
            if (mZoomView != null && child.bottom > mAppbarHeight && dy > 0 && type == ViewCompat.TYPE_TOUCH) {
                consumed[1] = dy
                zoomHeaderImageView(child, dy)
            } else {
                val valueAnimator = this.valueAnimator
                if (valueAnimator == null || !valueAnimator.isRunning) {
                    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                }
            }
        }
    }

    /**
     * 对ImageView进行缩放处理，对AppbarLayout进行高度的设置
     */
    private fun zoomHeaderImageView(abl: AppBarLayout, dy: Int) {
        mTotalDy += (-dy).toFloat()
        mTotalDy = Math.min(mTotalDy, maxZoomHeight)
        mScaleValue = Math.max(1f, 1f + mTotalDy / maxZoomHeight)
        mZoomView?.scaleX = mScaleValue
        mZoomView?.scaleY = mScaleValue
        mLastBottom = mAppbarHeight + (mImageViewHeight / 2 * (mScaleValue - 1)).toInt()
        abl.bottom = mLastBottom
    }


    /**
     * 处理惯性滑动的情况
     */
    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, velocityX: Float, velocityY: Float): Boolean {
        if (velocityY > 100) {
            isAnimate = false
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }


    /**
     * 滑动停止的时候，恢复AppbarLayout、ImageView的原始状态
     */
    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        recovery(abl)
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
    }

    /**
     * 通过属性动画的形式，恢复AppbarLayout、ImageView的原始状态
     */
    private fun recovery(abl: AppBarLayout) {
        if (mTotalDy > 0) {
            mTotalDy = 0f
            if (isAnimate) {
                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220).apply {
                    addUpdateListener { animation ->
                        val value = animation.animatedValue as Float
                        mZoomView?.scaleX = value
                        mZoomView?.scaleY = value
                        abl.bottom = (mLastBottom - (mLastBottom - mAppbarHeight) * animation.animatedFraction).toInt()
                    }
                    start()
                }
            } else {
                mZoomView?.scaleY = 1f
                mZoomView?.scaleY = 1f
                abl.bottom = mAppbarHeight
            }
        }
    }
}