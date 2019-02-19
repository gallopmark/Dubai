package com.uroad.dubai.activity

import android.Manifest
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CalendarListAdapter
import com.uroad.dubai.api.presenter.CalendarPresenter
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.CalendarMDL
import com.uroad.dubai.permission.PermissionHelper
import com.uroad.dubai.permission.PermissionInterface
import com.uroad.dubai.permission.PermissionUtil
import kotlinx.android.synthetic.main.content_smartrefresh.*
import kotlin.collections.ArrayList

class CalendarListActivity : BaseRefreshPresenterActivity<CalendarPresenter>(), CalendarView, PermissionInterface {

    private lateinit var data: MutableList<CalendarMDL>
    private lateinit var adapter: CalendarListAdapter
    private lateinit var mPermissionHelper: PermissionHelper


    override fun initViewData() {
        withTitle(getString(R.string.mine_calendar))
        mPermissionHelper = PermissionHelper(this@CalendarListActivity,
                this@CalendarListActivity)
        mPermissionHelper.requestPermissions()
        data = ArrayList()
        adapter = CalendarListAdapter(this, data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun initData() {
        super.initData()
        getList()
    }

    private fun getList() {
        if (PermissionUtil.hasPermission(this, Manifest.permission.READ_CALENDAR)
                && PermissionUtil.hasPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            presenter?.let {
                it.getCalendar(this@CalendarListActivity)
            }
            adapter.notifyDataSetChanged()
        } else {
            mPermissionHelper.requestPermissions()
        }
    }

    override fun onPullToRefresh() {
        data.clear()
        getList()
    }

    override fun onPullToLoadMore() {
        onFinishLoadMoreWithNoMoreData()
    }

    override fun createPresenter(): CalendarPresenter {
        return CalendarPresenter(this)
    }

    override fun loadCalendarSuccess(list: ArrayList<CalendarMDL>) {
        onPullToLoadSuccess()
        if (list.size != 0){
            data.clear()
            data.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    override fun loadError(e: String) {
        onPullToLoadSuccess()
        //showShortToast(e)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun getPermissionsRequestCode(): Int = 10000

    override fun getPermissions(): Array<String> =
            arrayOf(Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR)

    override fun requestPermissionsSuccess() {
        presenter?.let {
            it.getCalendar(this@CalendarListActivity)
        }
    }

    override fun requestPermissionsFail() {
        onPullToLoadSuccess()
    }

}


