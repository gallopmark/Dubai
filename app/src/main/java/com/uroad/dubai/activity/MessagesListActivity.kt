package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.uroad.dubai.R
import com.uroad.dubai.adapter.MessagesListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.api.presenter.MessagesPresenter
import com.uroad.dubai.api.view.MessagesView
import kotlinx.android.synthetic.main.activity_base_refresh.*

class MessagesListActivity : BaseRefreshPresenterActivity<MessagesPresenter>(), MessagesView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<MessagesMDL>
    private lateinit var adapter: MessagesListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.mine_messages))
        data = ArrayList()
        adapter = MessagesListAdapter(this,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun createPresenter(): MessagesPresenter = MessagesPresenter(this)

    override fun onPresenterCreate() {
        getMsgList()
    }

    private fun getMsgList(){
        handler.sendEmptyMessageAtTime(10,1500)
    }

    override fun onPullToRefresh() {
        index = 1
        getMsgList()
    }

    override fun onPullToLoadMore() {
        getMsgList()
    }

    override fun onGetNewList(list: MutableList<MessagesMDL>) {
        onPullToLoadSuccess()
        if (index == 1) {
            data.clear()
        }
        data.addAll(list)
        adapter.notifyDataSetChanged()
        if (list.size < size) {
            onFinishLoadMoreWithNoMoreData()
        }
        if (index == 1 && data.size == 0) {
            onPageNoData()
        } else {
            index++
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }

    private var handler : Handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            removeCallbacksAndMessages(null)
            onPullToLoadSuccess()
            data.clear()
            val mdl = MessagesMDL()
            mdl.type = "0"
            mdl.isnew = true
            mdl.title = "Accident on AI Kuwait Street. "
            mdl.time = "11:30"

            val mes = MessagesMDL()
            mes.type = "1"
            mes.isnew = false
            mes.title = "Major projects to be unveiled in d..."
            mes.time = "10:30"

            data.add(mdl)
            data.add(mes)
            onFinishLoadMoreWithNoMoreData()
            adapter.notifyDataSetChanged()
        }
    }
}