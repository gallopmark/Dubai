package com.uroad.dubai.fragment

import com.uroad.dubai.adapter.AttrNearFmListAdapter
import com.uroad.dubai.api.presenter.AttractionNearFMPresenter
import com.uroad.dubai.api.view.AttractionNearFMView
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.AttractionNearFMMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_base_refresh.*
import java.util.*

class AttractionNearFragment : RankFragment<AttractionNearFMPresenter>() , AttractionNearFMView{
    override fun createPresenter(): AttractionNearFMPresenter = AttractionNearFMPresenter(this)

    private var type : String? = null
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<AttractionNearFMMDL>
    private lateinit var adapter: AttrNearFmListAdapter

    override fun initViewData() {
        arguments?.let {
            type = it.getString("type")
        }
        data = ArrayList()
        adapter = AttrNearFmListAdapter(context,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }



    private fun getMsgList(){
        var code = when (type) {
            "1001002" -> NewsType.HOTEL.code        //酒店
            "1001003" -> NewsType.RESTAURANT.code   //餐厅
            else -> NewsType.ATTRACTION.code        //1001004 景点

        }
        presenter?.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(code, "", index, size))
        /*onPullToLoadSuccess()
        val mdl = AttractionNearFMMDL()
        var num = Random()
        mdl.imgPath = imgUrl[num.nextInt(4)]
        mdl.title = "Parking at the shopp..."
        mdl.content = "Location Distance Available"
        mdl.distance = "Financial Center Rd 0.5km 5"
        data.add(mdl)
        adapter.notifyDataSetChanged()*/
    }

    override fun onPullToRefresh() {
        index = 1
        getMsgList()
    }

    override fun onPullToLoadMore() {
        getMsgList()
    }

    override fun onGetNewList(list: MutableList<AttractionNearFMMDL>) {
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