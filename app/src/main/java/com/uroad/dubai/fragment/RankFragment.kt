package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.utils.ClassUtil
import kotlinx.android.synthetic.main.activity_base_refresh.*

abstract class RankFragment< P : BasePresenter<*>> : BaseFragment() , BaseView {

    open var presenter: P? = null

    protected abstract fun createPresenter(): P

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_base_refresh)
        //presenter = ClassUtil.getT(this,0)

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
    }

    open fun initViewData() {

    }

    open fun onPullToRefresh() {

    }

    open fun onPullToLoadMore() {

    }

    open fun onPresenterCreate() {}

    override fun initData() {
        presenter = createPresenter()
        onPresenterCreate()
    }

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

}