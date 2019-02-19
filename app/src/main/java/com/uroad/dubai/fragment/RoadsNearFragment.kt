package com.uroad.dubai.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.uroad.dubai.adapter.RoadsNearFmListAdapter
import com.uroad.dubai.api.presenter.RoadsNearFMPresenter
import com.uroad.dubai.api.view.RoadsNearFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.RoadsMDL
import com.uroad.dubai.webService.WebApi
import java.util.*

class RoadsNearFragment : BasePageRefreshPresenterFragment<RoadsNearFMPresenter>(), RoadsNearFMView {
    override fun createPresenter(): RoadsNearFMPresenter = RoadsNearFMPresenter(this)

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<RoadsMDL>
    private lateinit var adapter: RoadsNearFmListAdapter

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        data = ArrayList()
        adapter = RoadsNearFmListAdapter(context, data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {

            }
        })
    }

    private fun getMsgList() {
        presenter?.getNewsList(WebApi.GET_ROADS_LIST, WebApi.getRoadListParams(index, size, "", 0.0, 0.0))
    }

    override fun onPullToRefresh() {
        index = 1
        getMsgList()
    }

    override fun onPullToLoadMore() {
        getMsgList()
    }

    override fun onGetNewList(list: MutableList<RoadsMDL>) {
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

        data.forEach {
            it.colors = getMDL()
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }

    private fun getMDL(): ArrayList<Int> {
        return ArrayList<Int>().apply {
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#ED1C24"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#F7F30A"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#ED1C24"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
            add(Color.parseColor("#06A72B"))
        }

    }
}