package com.uroad.dubai.fragment

import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.uroad.dubai.R
import com.uroad.dubai.activity.EventsDetailActivity
import com.uroad.dubai.adapter.FavoriteEventFmListAdapter
import com.uroad.dubai.api.presenter.FavoriteEventFMPresenter
import com.uroad.dubai.api.view.FavoriteEventFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL
import java.util.*

class FavoriteEventFragment  : BasePageRefreshPresenterFragment<FavoriteEventFMPresenter>() , FavoriteEventFMView {

    private var type : String? = null
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<EventsMDL>
    private lateinit var adapter : FavoriteEventFmListAdapter

    override fun initViewData(view: View, savedInstanceState: Bundle?) {
        data = ArrayList()
        adapter = FavoriteEventFmListAdapter(context,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                openActivity(EventsDetailActivity::class.java,Bundle().apply {
                    putString("eventMDL",Gson().toJson( data[position]))
                })
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
        data.clear()
        val mdl = getMDL(Random().nextInt(5)-1)
        data.add(mdl)
        data.add(getMDL(0))
        data.add(getMDL(1))
        data.add(getMDL(2))
        adapter.notifyDataSetChanged()
        onFinishLoadMoreWithNoMoreData()
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

    private fun getMDL(position: Int): EventsMDL {
        return EventsMDL().apply {
            iconInt = when (position) {
                0 -> R.mipmap.ic_roads_e11
                1 -> R.mipmap.ic_roads_e44
                2 -> R.mipmap.ic_roads_e311
                else -> R.mipmap.ic_roads_e66
            }
            updatetime = when(position){
                0 -> "58 mins ago"
                1 -> "1h ago"
                2 -> "12/11 14:22"
                else -> "1/15 16:17"
            }
            roadtitle = "Dubai to ABU dhabi"
            reportout = "On the morning of 6 February, 22 people were injured in a major road accident along sheikh M-ohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
        }
    }
}