package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adapter.RestaurantsListCardAdapter
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeRestaurantsFragment : BasePresenterFragment<NewsPresenter>(), NewsView {

    private val data = ArrayList<NewsMDL>()
    private lateinit var adapter: RestaurantsListCardAdapter
    private val handler = Handler()

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = RestaurantsListCardAdapter(context, data)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                val mdl = data[position]
                val scenicMDL = ScenicMDL()
                scenicMDL.headimg = mdl.headimg
                scenicMDL.title = mdl.title
                scenicMDL.content = mdl.content
                scenicMDL.address = mdl.address
                scenicMDL.hours = mdl.hours
                scenicMDL.latitude = mdl.latitude
                scenicMDL.longitude = mdl.longitude
                scenicMDL.phone = mdl.phone
                DubaiApplication.clickItemScenic = scenicMDL
                openActivity(ScenicDetailActivity::class.java)
            }
        })
    }

    override fun initData() {
        presenter.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.RESTAURANT.code, "", 1, 4))
    }

    override fun onGetNewList(news: MutableList<NewsMDL>) {
        this.data.clear()
        this.data.addAll(news)
        adapter.notifyDataSetChanged()
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}