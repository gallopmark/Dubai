package com.uroad.dubai.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.PoliceListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.PoliceMDL
import com.uroad.dubai.api.presenter.PolicePresenter
import com.uroad.dubai.api.view.PoliceView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.library.utils.PhoneUtils
import kotlinx.android.synthetic.main.activity_base_refresh.*

class PoliceListActivity: BaseRefreshPresenterActivity<PolicePresenter>(), PoliceView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<PoliceMDL>
    private lateinit var adapter: PoliceListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.travel_menu_police))
        data = ArrayList()
        adapter = PoliceListAdapter(this,data).apply {
            setOnItemChildClickListener(object :BaseRecyclerAdapter.OnItemChildClickListener{
                override fun onItemChildClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    PhoneUtils.call(this@PoliceListActivity,"1456")
                }
            })
        }
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }


    override fun initData() {
        super.initData()
        getList()

    }


    private fun getList(){
        onPullToLoadSuccess()
        val mdl = PoliceMDL()
        data.add(mdl)
        adapter.notifyDataSetChanged()
    }

    override fun onPullToRefresh() {
        index = 1
        getList()
    }

    override fun onPullToLoadMore() {
        getList()
    }

    override fun onGetNewList(list: MutableList<PoliceMDL>) {
        onPullToLoadSuccess()
        if (index == 1) {
            data.clear()
        }
        data.addAll(list)
        adapter.notifyDataSetChanged()
        if (list.size < size) {
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

    override fun createPresenter(): PolicePresenter = PolicePresenter(this)
}