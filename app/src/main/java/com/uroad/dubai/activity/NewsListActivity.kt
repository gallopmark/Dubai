package com.uroad.dubai.activity

import android.os.Bundle
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.NewsListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.content_smartrefresh.*

/**
 * @author MFB
 * @create 2018/12/21
 * @describe News list activity
 */
class NewsListActivity : BaseRefreshPresenterActivity<NewsPresenter>(), NewsView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<NewsMDL>
    private lateinit var adapter: NewsListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.home_menu_news))
        data = ArrayList()
        adapter = NewsListAdapter(this, data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()

        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                var mdl = data[position]
                openActivity(NewsDetailsActivity::class.java, Bundle().apply {
                    putString("newsId", mdl.newsid)
                    putString("title", getString(R.string.home_menu_news))
                })
            }
        })
    }

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)

    override fun initData() {
        presenter.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.NEWS.code, "", index, size, 0.0, 0.0))
    }

    override fun onPullToRefresh() {
        index = 1
        initData()
    }

    override fun onPullToLoadMore() {
        initData()
    }

    override fun onGetNewList(news: MutableList<NewsMDL>) {
        onPullToLoadSuccess()
        if (index == 1) {
            data.clear()
        }
        data.addAll(news)
        adapter.notifyDataSetChanged()
        if (news.size < size) {
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