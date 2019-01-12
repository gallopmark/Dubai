package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.DetailsActivity
import com.uroad.dubai.adapter.NewsListAdapter
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeNewsFragment : BasePresenterFragment<NewsPresenter>(), NewsView {

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)
    private val data = ArrayList<NewsMDL>()
    private lateinit var adapter: NewsListAdapter
    private val handler = Handler()

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = NewsListAdapter(context, data)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                val mdl = data[position]
                openActivity(DetailsActivity::class.java, Bundle().apply {
                    putString("title",mdl.title)
                    putString("time",mdl.publishtime)
                    putString("imgUrl",mdl.headimg)
                    putString("content",mdl.content)
                })
            }
        })
    }


    override fun initData() {
        presenter?.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.NEWS.code, "", 1, 4))
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