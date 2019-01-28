package com.uroad.dubai.fragment

import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.uroad.dubai.R
import com.uroad.dubai.activity.EventsDetailActivity
import com.uroad.dubai.adapter.FavoriteEventFmListAdapter
import com.uroad.dubai.api.presenter.EventsPresenter
import com.uroad.dubai.api.presenter.FavoriteEventFMPresenter
import com.uroad.dubai.api.presenter.RoadsNearFMPresenter
import com.uroad.dubai.api.view.FavoriteEventFMView
import com.uroad.dubai.api.view.RoadsNearFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.model.RoadsMDL
import java.util.*

class FavoriteEventFragment : BasePageRefreshPresenterFragment<EventsPresenter>(), EventsPresenter.EventsView {

    companion object {
        fun newInstance(longitude: Double, latitude: Double): FavoriteEventFragment {
            return FavoriteEventFragment().apply {
                arguments = Bundle().apply {
                    putDouble("longitude", longitude)
                    putDouble("latitude", latitude)
                }
            }
        }
    }


    private var type: String? = null
    private var index = 1
    private var size = 10
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private lateinit var data: MutableList<EventsMDL>
    private lateinit var adapter: FavoriteEventFmListAdapter

    override fun initViewData(view: View, savedInstanceState: Bundle?) {
        initBundle()
        data = ArrayList()
        adapter = FavoriteEventFmListAdapter(context, data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                openActivity(EventsDetailActivity::class.java, Bundle().apply {
                    putString("eventMDL", Gson().toJson(data[position]))
                })
            }
        })
    }

    private fun initBundle() {
        arguments?.let {
            longitude = it.getDouble("longitude", 0.0)
            latitude = it.getDouble("latitude", 0.0)
        }
    }

    override fun onPullToRefresh() {
        index = 1
        getMsgList()
    }

    override fun onPullToLoadMore() {
        getMsgList()
    }

    override fun onGetEventList(data: MutableList<EventsMDL>) {
        if (index == 1) {
            data.clear()
        }
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
        if (this.data.size < size) {
            onFinishLoadMoreWithNoMoreData()
        }
        if (index == 1 && data.size == 0) {
            onPageNoData()
        } else {
            index++
        }
    }

    private fun getMsgList() {
        presenter?.getEventList(index,size,"",longitude,latitude)
    }

    override fun createPresenter(): EventsPresenter? = EventsPresenter(this)
}