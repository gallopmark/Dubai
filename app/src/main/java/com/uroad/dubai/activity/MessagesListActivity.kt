package com.uroad.dubai.activity

import com.uroad.dubai.R
import com.uroad.dubai.adapter.MessagesListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.api.presenter.MessagesPresenter
import com.uroad.dubai.api.view.MessagesView
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_base_refresh.*

class MessagesListActivity : BaseRefreshPresenterActivity<MessagesPresenter>(), MessagesView {

    private var useruuid : String = ""
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
        useruuid = UserPreferenceHelper.getUserUUID(this@MessagesListActivity)?:""
    }

    override fun createPresenter(): MessagesPresenter = MessagesPresenter(this)

    override fun initData() {
        getMsgList()
    }

    private fun getMsgList(){
         presenter?.messageCenter(WebApi.MESSAGECENTER,WebApi.messageCenter(useruuid,index,size))
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
}