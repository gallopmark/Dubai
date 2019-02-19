package com.uroad.dubai.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import kotlinx.android.synthetic.main.content_smartrefresh.*

/**
 * @author MFB
 * @create 2018/12/21
 * @describe
 */
abstract class BaseRefreshPresenterActivity<P : BasePresenter<*>> : BaseDubaiActivity(), BaseView {
    open lateinit var presenter: P

    protected abstract fun createPresenter(): P

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_base_refresh)
        presenter = createPresenter()
        onPresenterSetUp(savedInstanceState)
        val layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
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
    }

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

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }
}