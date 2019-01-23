package com.uroad.dubai.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.uroad.dubai.adapter.FavoriteRouteFmListAdapter
import com.uroad.dubai.api.presenter.FavoriteRouteFMPresenter
import com.uroad.dubai.api.view.FavoriteRouteFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.RouteMDL
import java.util.*

class FavoriteRouteFragment  : BasePageRefreshPresenterFragment<FavoriteRouteFMPresenter>() , FavoriteRouteFMView {

    private var type : String? = null
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<RouteMDL>
    private lateinit var adapter : FavoriteRouteFmListAdapter

    override fun initViewData(view: View, savedInstanceState: Bundle?) {
        data = ArrayList()
        adapter = FavoriteRouteFmListAdapter(context,data)
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
        data.clear()
        data.add(getMDL(Random().nextInt(4)-1))
        adapter.notifyDataSetChanged()
        onFinishLoadMoreWithNoMoreData()
    }

    override fun createPresenter(): FavoriteRouteFMPresenter? {
        return FavoriteRouteFMPresenter(this)
    }

    override fun onGetNewList(list: MutableList<RouteMDL>) {
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

    private fun getMDL(position: Int): RouteMDL{
        return RouteMDL().apply {
            title = when(position){
                0-> "Home-Burj AI Arab"
                1-> "Home-Dubai museum"
                2-> "Home-Dubai aquarium and..."
                else -> "Home-Gold street, dubai"
            }
            colors = ArrayList<Int>().apply {
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#ED1C24"))
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
            }
        }
    }
}