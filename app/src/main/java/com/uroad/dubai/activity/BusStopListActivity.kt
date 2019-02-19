package com.uroad.dubai.activity

import com.uroad.dubai.R
import com.uroad.dubai.adapter.BusStopListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.BusStopMDL
import com.uroad.dubai.api.presenter.BusStopPresenter
import com.uroad.dubai.api.view.BusStopView
import kotlinx.android.synthetic.main.content_smartrefresh.*

class BusStopListActivity: BaseRefreshPresenterActivity<BusStopPresenter>(), BusStopView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<BusStopMDL>
    private lateinit var adapter: BusStopListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.travel_menu_bus_stop))
        data = ArrayList()
        adapter = BusStopListAdapter(this,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun initData() {
        super.initData()
        getList()
    }

    private fun getList(){
        onPullToLoadSuccess()
        val mdl = BusStopMDL()
        data.add(mdl)
        adapter.notifyDataSetChanged()
    }

    override fun onPullToRefresh() {
        index = 1
        getList()
    }

    override fun onPullToLoadMore() {
        getList()
    }

    override fun onGetNewList(list: MutableList<BusStopMDL>) {
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

    override fun createPresenter(): BusStopPresenter = BusStopPresenter(this)
}