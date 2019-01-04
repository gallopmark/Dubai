package com.uroad.dubai.fragment

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.RestaurantsListAdapter
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeRestaurantsFragment : BasePresenterFragment<NewsPresenter>(), NewsView {

    private val data = ArrayList<NewsMDL>()
    private lateinit var adapter: RestaurantsListAdapter
    private val handler = Handler()

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)

    override fun onPresenterSetUp(view: View) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = RestaurantsListAdapter(context, data)
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter?.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.RESTAURANT.code, "", 1, 4))
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