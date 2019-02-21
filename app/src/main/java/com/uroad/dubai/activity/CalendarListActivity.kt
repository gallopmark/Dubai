package com.uroad.dubai.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CalendarListAdapter
import com.uroad.dubai.api.presenter.CalendarPresenter
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.model.CalendarMDL
import kotlinx.android.synthetic.main.content_smartrefresh.*
import kotlin.collections.ArrayList

class CalendarListActivity : BaseRefreshPresenterActivity<CalendarPresenter>(), CalendarView {

    private lateinit var data: MutableList<CalendarMDL>
    private lateinit var adapter: CalendarListAdapter


    override fun initViewData() {
        withTitle(getString(R.string.mine_calendar))
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED){

                presenter?.let {
                    it.getCalendar(this@CalendarListActivity)
                }

                adapter.notifyDataSetChanged()

            }else{
                requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR),10000)
            }

        }else{
            presenter?.let {
                it.getCalendar(this@CalendarListActivity)
            }
            adapter.notifyDataSetChanged()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10000){
            var isAllGranted = true//是否全部权限已授权
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false
                    break
                }
            }
            if (isAllGranted){
                presenter?.let {
                    it.getCalendar(this@CalendarListActivity)
                }
            }else{
                onPullToLoadSuccess()
            }
        }
    }

}


