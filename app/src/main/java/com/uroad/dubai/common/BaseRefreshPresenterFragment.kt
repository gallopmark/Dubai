package com.uroad.dubai.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import kotlinx.android.synthetic.main.content_smartrefresh.*

abstract class BaseRefreshPresenterFragment<P : BasePresenter<*>> : BaseDubaiFragment(), BaseView {
    open var presenter: P? = null

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_base_refresh)
        val layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        recyclerView.layoutManager = layoutManager
        initViewData()
        baseRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                onPullToRefresh()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                onPullToLoadMore()
            }
        })
        presenter = createPresenter()
        onPresenterSetUp(savedInstanceState)
    }

    abstract fun createPresenter(): P?
    open fun onPresenterSetUp(savedInstanceState: Bundle?) {

    }

    open fun initViewData() {

    }

    open fun onPullToRefresh() {

    }

    open fun onPullToLoadMore() {

    }

    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {
        onPullToLoadError()
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

    override fun onDestroyView() {
        presenter?.detachView()
        super.onDestroyView()
    }
}