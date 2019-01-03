package com.uroad.dubai.activity

import com.uroad.dubai.R
import com.uroad.dubai.adapter.CalendarListAdapter
import com.uroad.dubai.api.presenter.CalendarPresenter
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.CalendarMDL
import kotlinx.android.synthetic.main.activity_base_refresh.*

class CalendarListActivity : BaseRefreshPresenterActivity<CalendarPresenter>(),CalendarView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<CalendarMDL>
    private lateinit var adapter: CalendarListAdapter


    override fun initViewData() {
        withTitle(getString(R.string.mine_calendar))
        data = ArrayList()
        adapter = CalendarListAdapter(this,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun initData() {
        super.initData()
        getList()
    }

    private fun getList(){
        onPullToLoadSuccess()
        if (index < 10){
            val mdl = CalendarMDL()
            data.add(mdl)
            adapter.notifyDataSetChanged()
        }else{
            onFinishLoadMoreWithNoMoreData()
            return
        }
        index++
    }

    override fun onPullToRefresh() {
        index = 1
        data.clear()
        getList()
    }

    override fun onPullToLoadMore() {
        getList()
    }

    override fun createPresenter(): CalendarPresenter {
        return CalendarPresenter(this)
    }

    override fun onGetNewList(list: MutableList<CalendarMDL>) {
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