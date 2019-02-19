package com.uroad.dubai.activity

import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.NoticeListAdapter
import com.uroad.dubai.api.presenter.NoticePresenter
import com.uroad.dubai.api.view.NoticeView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.dialog.NoticeDialog
import com.uroad.dubai.model.NoticeMDL
import kotlinx.android.synthetic.main.content_smartrefresh.*

/**
 * @author MFB
 * @create 2019/1/23
 * @describe notice list
 */
class NoticeListActivity : BaseRefreshPresenterActivity<NoticePresenter>(), NoticeView {

    private lateinit var data: MutableList<NoticeMDL>
    private lateinit var adapter: NoticeListAdapter
    private var isFirstLoad = true
    override fun createPresenter(): NoticePresenter = NoticePresenter(this)

    override fun initViewData() {
        withTitle(getString(R.string.notices))
        data = ArrayList()
        adapter = NoticeListAdapter(this, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until data.size) {
                        NoticeDialog(this@NoticeListActivity)
                                .setCustomTitle(data[position].publishtime)
                                .setContent(data[position].content)
                                .show()
                    }
                }
            })
        }
        recyclerView.adapter = adapter
        baseRefreshLayout.setEnableLoadMore(false)
    }

    override fun initData() {
        presenter.getNotices()
    }

    override fun onPullToRefresh() {
        initData()
    }

    override fun onShowLoading() {
        if (isFirstLoad) onPageLoading()
    }

    override fun getNoticeList(data: MutableList<NoticeMDL>) {
        if (isFirstLoad) {
            onPageResponse()
            isFirstLoad = false
        } else {
            onRefreshSuccess()
        }
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        if (isFirstLoad) onPageError()
        else onRefreshFailed()
    }
}