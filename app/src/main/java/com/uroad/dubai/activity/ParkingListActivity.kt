package com.uroad.dubai.activity

import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.uroad.dubai.R
import com.uroad.dubai.adapter.ParkingListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.ParkingMDL
import com.uroad.dubai.webService.api.presenter.ParkingPresenter
import com.uroad.dubai.webService.api.view.ParkingView
import kotlinx.android.synthetic.main.activity_base_refresh.*

class ParkingListActivity: BaseRefreshPresenterActivity<ParkingPresenter>(), ParkingView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<ParkingMDL>
    private lateinit var adapter: ParkingListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.travel_menu_parking))
        //withOption(ContextCompat.getDrawable(this,R.mipmap.ic_search_grey),"search")
        data = ArrayList()
        adapter = ParkingListAdapter(this,data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.id_menu_search -> {
                showShortToast("程序员正在日以继夜的开发中...")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPresenterCreate() {
        getList()
    }

    private fun getList(){
        onPullToLoadSuccess()
        val mdl = ParkingMDL()
        mdl.imgPath = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1545730732974&di=40dd62557d68ca4d98a69e65bfa09b29&imgtype=0&src=http%3A%2F%2Fimg1d.xgo-img.com.cn%2Fpics%2F1538%2F1537599.jpg"
        mdl.title = "Parking at the shopp..."
        mdl.content = "Location Distance Available"
        mdl.distance = "Financial Center Rd 0.5km 5"
        mdl.num = "Available "+data.size.toString()
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

    override fun onGetNewList(list: MutableList<ParkingMDL>) {
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

    override fun createPresenter(): ParkingPresenter = ParkingPresenter(this)
}