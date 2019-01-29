package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.NewsDetailsActivity
import com.uroad.dubai.adapter.NewsListCardAdapter
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeNewsFragment : NearMeBaseFragment<NewsPresenter>(), NewsView {

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)
    private val data = ArrayList<NewsMDL>()
    private lateinit var adapter: NewsListCardAdapter

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = NewsListCardAdapter(context, data)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                if (position !in 0 until data.size) return
                openActivity(NewsDetailsActivity::class.java, Bundle().apply { putString("newsId", data[position].newsid) })
            }
        })
    }


    override fun initData() {
        presenter.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.NEWS.code, "", 1, 4, longitude, latitude))
    }

    override fun onGetNewList(news: MutableList<NewsMDL>) {
        this.data.clear()
        this.data.addAll(news)
        adapter.notifyDataSetChanged()
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        onRetry()
    }

    override fun onShowError(msg: String?) {
        onRetry()
    }
}