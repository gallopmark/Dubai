package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.activity.NoticeListActivity
import com.uroad.dubai.api.presenter.NoticePresenter
import com.uroad.dubai.api.view.NoticeView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.NoticeMDL
import kotlinx.android.synthetic.main.fragment_mainnotice.*
import java.lang.ref.WeakReference

class MainNoticeFragment : BasePresenterFragment<NoticePresenter>(), NoticeView {

    override fun createPresenter(): NoticePresenter = NoticePresenter(this)

    private var data: MutableList<NoticeMDL> = ArrayList()
    private var mPosition: Int = 0
    private lateinit var handler: MHandler
    private var callback: OnRequestCallback? = null
    companion object {
        private const val CODE_MESSAGE = 100
        private const val CODE_RETRY = 110
    }

    private class MHandler(fragment: MainNoticeFragment) : Handler() {
        private val weakReference = WeakReference<MainNoticeFragment>(fragment)
        override fun handleMessage(msg: Message) {
            val fragment = weakReference.get() ?: return
            when (msg.what) {
                CODE_MESSAGE -> {
                    fragment.mPosition++
                    fragment.textSwitcher.setCurrentText(fragment.data[fragment.mPosition % fragment.data.size].content)
                    if (fragment.mPosition == fragment.data.size) {
                        fragment.mPosition = 0
                    }
                    sendEmptyMessageDelayed(CODE_MESSAGE, 5000)
                }
                CODE_RETRY -> {
                    fragment.initData()
                }
            }
        }
    }

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainnotice)
        initSwitcher()
        handler = MHandler(this)
    }

    private fun initSwitcher() {
        textSwitcher.setFactory { return@setFactory customTextView() }
        textSwitcher.setOnClickListener { openActivity(NoticeListActivity::class.java) }
    }

    private fun customTextView(): TextView {
        return TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.font_14))
            minLines = 2
            maxLines = 2
            gravity = Gravity.CENTER or Gravity.START
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(ContextCompat.getColor(context, R.color.grey))
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.CENTER }
        }
    }

    override fun initData() {
        presenter.getNotices()
    }

    override fun getNoticeList(data: MutableList<NoticeMDL>) {
        this.data.clear()
        this.data.addAll(data)
        handler.removeMessages(CODE_MESSAGE)
        if (!this.data.isEmpty()) {
            textSwitcher.setText(this.data[0].content)
            handler.sendEmptyMessageDelayed(CODE_MESSAGE, 5000)
        }
        callback?.callback(this.data.isEmpty())
    }

    override fun onShowError(msg: String?) {
        handler.sendEmptyMessageDelayed(CODE_RETRY, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        handler.sendEmptyMessageDelayed(CODE_RETRY, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    interface OnRequestCallback {
        fun callback(isEmpty: Boolean)
        fun onFailure()
    }

    fun setOnRequestCallback(callback: OnRequestCallback) {
        this.callback = callback
    }
}