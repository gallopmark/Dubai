package com.uroad.dubai.activity

import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.MessagesListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.api.presenter.MessagesPresenter
import com.uroad.dubai.api.view.MessagesView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.dialog.NoticeDialog
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_base_refresh.*

class MessagesListActivity : BaseRefreshPresenterActivity<MessagesPresenter>(), MessagesView, MessagesPresenter.MessageDetailView {
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<MessagesMDL>
    private lateinit var adapter: MessagesListAdapter
    private var clickIndex = -1

    override fun initViewData() {
        withTitle(getString(R.string.mine_messages))
        data = ArrayList()
        adapter = MessagesListAdapter(this, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position !in 0 until data.size) return
                    onMessageDetail(position)
                    NoticeDialog(this@MessagesListActivity).setCustomTitle(data[position].created).setContent(data[position].content)
                            .show()
                }
            })
        }
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    private fun onMessageDetail(position: Int) {
        clickIndex = position
        presenter.messageDetail(data[position].messageid, this)
    }

    override fun createPresenter(): MessagesPresenter = MessagesPresenter(this)

    override fun initData() {
        getMsgList()
    }

    private fun getMsgList() {
        presenter.messageCenter(WebApi.MESSAGE_CENTER, WebApi.messageCenter("", getUserUUID(), index, size))
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

    override fun onShowError(msg: String?) {
        onPageError()
    }

    override fun onSuccess(data: String?) {
        this.data[clickIndex].readstatus = "1"
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(errorMsg: String?) {
    }
}