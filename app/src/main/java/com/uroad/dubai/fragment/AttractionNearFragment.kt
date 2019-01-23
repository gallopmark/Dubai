package com.uroad.dubai.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adapter.AttrNearFmListAdapter
import com.uroad.dubai.api.presenter.AttractionNearFMPresenter
import com.uroad.dubai.api.view.AttractionNearFMView
import com.uroad.dubai.common.BasePageRefreshPresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.AttractionNearFMMDL
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import java.util.*

class AttractionNearFragment : BasePageRefreshPresenterFragment<AttractionNearFMPresenter>() , AttractionNearFMView{
    override fun createPresenter(): AttractionNearFMPresenter = AttractionNearFMPresenter(this)

    private var type : String? = null
    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<AttractionNearFMMDL>
    private lateinit var adapter: AttrNearFmListAdapter

    override fun initViewData(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getString("type")
        }
        data = ArrayList()
        adapter = AttrNearFmListAdapter(context,type,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                val mdl = data[position]
                val scenicMDL = ScenicMDL()
                scenicMDL.headimg = mdl.headimg
                scenicMDL.title = mdl.title
                scenicMDL.content = mdl.content
                scenicMDL.address = mdl.address
                scenicMDL.latitude = mdl.latitude
                scenicMDL.longitude = mdl.longitude
                scenicMDL.hours = if (TextUtils.isEmpty(mdl.time)) mdl.hours else mdl.time
                scenicMDL.phone = mdl.phone
                DubaiApplication.clickItemScenic = scenicMDL
                openActivity(ScenicDetailActivity::class.java)
                /*if(type.equals("1001004")){
                    return
                }
                openActivity(DetailsActivity::class.java,Bundle().apply {
                    putString("title",mdl.title)
                    putString("time",mdl.hours)
                    putString("imgUrl",mdl.headimg)
                    putString("content",mdl.content)
                })*/
            }
        })
    }



    private fun getMsgList(){
        val code = when (type) {
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