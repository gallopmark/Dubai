package com.uroad.dubai.fragment

import android.view.View
import com.uroad.dubai.adapter.FavoriteEventFmListAdapter
import com.uroad.dubai.api.presenter.FavoriteEventFMPresenter
import com.uroad.dubai.api.view.FavoriteEventFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL

class FavoriteEventFragment  : BasePageRefreshPresenterFragment<FavoriteEventFMPresenter>() , FavoriteEventFMView {

    private var type : String? = null
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<EventsMDL>
    private lateinit var adapter : FavoriteEventFmListAdapter

    override fun initViewData() {
        data = ArrayList()
        adapter = FavoriteEventFmListAdapter(context,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {

            }
        })
    }

    override fun onPullToRefresh() {
        index = 1
        getMsgList()
    }

    override fun onPullToLoadMore() {
        getMsgList()
    }

    private fun getMsgList(){
        onPullToLoadSuccess()
        data.add(EventsMDL())
        adapter.notifyDataSetChanged()
    }

    override fun createPresenter(): FavoriteEventFMPresenter? = FavoriteEventFMPresenter(this)

    override fun onGetNewList(list: MutableList<EventsMDL>) {
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