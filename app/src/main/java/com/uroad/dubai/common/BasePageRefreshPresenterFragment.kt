package com.uroad.dubai.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePageRefreshPresenterFragment<P : BasePresenter<*>> : BasePageFragment(), BaseView {
    open lateinit var baseRefreshLayout: SmartRefreshLayout
    open lateinit var recyclerView: RecyclerView
    open var presenter: P? = null

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_base_refresh)
        baseRefreshLayout = view.findViewById(R.id.baseRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        recyclerView.layoutManager = layoutManager
        baseRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                onPullToRefresh()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                onPullToLoadMore()
            }
        })
        initViewData(view, savedInstanceState)
        presenter = createPresenter()
    }

    abstract fun initViewData(view: View, savedInstanceState: Bundle?)
    abstract fun onPullToRefresh()
    abstract fun onPullToLoadMore()

    abstract fun createPresenter(): P?

    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {

    }

    open fun onRefreshSuccess() {
        baseRefreshLayout.finishRefresh(true)
    }

    open fun onRefreshFailed() {
        baseRefreshLayout.finishRefresh(false)
        baseRefreshLayout.resetNoMoreData()
    }

    open fun onLoadMoreSuccess() {
        baseRefreshLayout.finishLoadMore(true)
    }

    open fun onLoadMoreFailed() {
        baseRefreshLayout.finishLoadMore(false)
        baseRefreshLayout.resetNoMoreData()
    }

    open fun onPullToLoadSuccess() {
        baseRefreshLayout.finishRefresh(true)
        baseRefreshLayout.finishLoadMore(true)
    }

    open fun onPullToLoadError() {
        baseRefreshLayout.finishRefresh(false)
        baseRefreshLayout.finishLoadMore(false)
        baseRefreshLayout.resetNoMoreData()
    }

    open fun onFinishLoadMoreWithNoMoreData() {
        baseRefreshLayout.finishLoadMoreWithNoMoreData()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }
}