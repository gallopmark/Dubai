package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.NoticeListActivity
import com.uroad.dubai.api.presenter.FunctionStatisticsPresenter
import com.uroad.dubai.api.presenter.NoticePresenter
import com.uroad.dubai.api.view.NoticeView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.StatisticsType
import com.uroad.dubai.model.NoticeMDL
import kotlinx.android.synthetic.main.fragment_mainnotice.*

class MainNoticeFragment : BasePresenterFragment<NoticePresenter>(), NoticeView {

    override fun createPresenter(): NoticePresenter = NoticePresenter(this)

    private var data: MutableList<NoticeMDL> = ArrayList()
    private lateinit var handler: Handler
    private var callback: OnRequestCallback? = null
    private lateinit var statisticsPresenter: FunctionStatisticsPresenter

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainnotice)
        initNoticeView()
        statisticsPresenter = FunctionStatisticsPresenter(context)
        handler = Handler()
    }

    private fun initNoticeView() {
        llNotice.setOnClickListener {
            statisticsPresenter.onFuncStatistics(StatisticsType.HOME_NOTICE.CODE)
            openActivity(NoticeListActivity::class.java)
        }
    }

    override fun initData() {
        presenter.getNotices()
    }

    override fun getNoticeList(data: MutableList<NoticeMDL>) {
        this.data.clear()
        this.data.addAll(data)
        val list = ArrayList<String?>()
        for (item in this.data) {
            list.add(item.content)
        }
        noticeView.setTipList(list)
        callback?.callback(this.data.isEmpty())
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        statisticsPresenter.detachView()
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