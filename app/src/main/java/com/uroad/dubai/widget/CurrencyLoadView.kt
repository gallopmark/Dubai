package com.uroad.dubai.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.uroad.dubai.R

class CurrencyLoadView : FrameLayout {
    private val mContext: Context
    private var onRetryListener: OnRetryListener? = null

    companion object {
        const val STATE_IDEA = 0
        const val STATE_LOADING = 1
        const val STATE_NO_NETWORK = 2
        const val STATE_ERROR = 3
        const val STATE_EMPTY = 4
        const val STATE_GONE = 5
    }

    constructor(context: Context) : super(context) {
        mContext = context
        setState(STATE_IDEA)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        setState(STATE_IDEA)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        setState(STATE_IDEA)
    }

    fun setLoadingText(text: CharSequence?) {
        val mLoadingTv = findViewById<TextView>(R.id.mLoadingTv)
        mLoadingTv?.text = text
    }

    fun setEmptyIco(resId: Int) {
        val mEmptyTv = findViewById<TextView>(R.id.mEmptyTv)
        val drawableTop = ContextCompat.getDrawable(mContext, resId)
        mEmptyTv?.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
    }

    fun setEmptyText(text: CharSequence?) {
        val mEmptyTv = findViewById<TextView>(R.id.mEmptyTv)
        mEmptyTv?.text = text
    }

    fun setErrorText(text: CharSequence?) {
        val mErrorTv = findViewById<TextView>(R.id.mErrorTv)
        mErrorTv?.text = text
    }

    fun setErrorIcon(resId: Int) {
        val mErrorTv = findViewById<TextView>(R.id.mErrorTv)
        val drawableTop = ContextCompat.getDrawable(mContext, resId)
        mErrorTv?.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
    }

    fun setState(state: Int) {
        when (state) {
            STATE_IDEA -> onIdea()
            STATE_LOADING -> onLoading()
            STATE_NO_NETWORK -> onError(1)
            STATE_ERROR -> onError(2)
            STATE_EMPTY -> onEmpty()
            STATE_GONE -> visibility = View.GONE
        }
    }

    private fun onIdea() {
        clearViews()
        visibility = View.GONE
    }

    private fun onLoading() {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading, LinearLayout(context), false)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        clearViews()
        addView(view, params)
        visibility = View.VISIBLE
    }

    private fun onError(type: Int) {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_error, LinearLayout(context), false)
        clearViews()
        addView(view)
        visibility = View.VISIBLE
        if (type == 1) {
            setErrorText(resources.getString(R.string.nonetwork))
            setErrorIcon(R.mipmap.ic_nonetwork)
        } else {
            setErrorText(resources.getString(R.string.connect_error))
            setErrorIcon(R.mipmap.ic_nonetwork)
        }
        findViewById<TextView>(R.id.tvReload).setOnClickListener {
            setState(STATE_IDEA)
            onRetryListener?.onRetry(this@CurrencyLoadView)
        }
    }

    private fun onEmpty() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_empty, LinearLayout(context), false)
        addView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = mContext.resources.getDimensionPixelOffset(R.dimen.space_30)
            rightMargin = mContext.resources.getDimensionPixelOffset(R.dimen.space_30)
        })
        clearViews()
        addView(view)
        visibility = View.VISIBLE
    }

    private fun clearViews() {
        removeAllViews()
    }

    interface OnRetryListener {
        fun onRetry(view: View)
    }

    fun setOnRetryListener(onRetryListener: OnRetryListener) {
        this.onRetryListener = onRetryListener
    }
}