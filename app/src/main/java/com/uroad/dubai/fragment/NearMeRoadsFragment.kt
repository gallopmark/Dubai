package com.uroad.dubai.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.RoadsNearFmListAdapter
import com.uroad.dubai.api.presenter.RoadsNearFMPresenter
import com.uroad.dubai.api.view.RoadsNearFMView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.RoadsMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeRoadsFragment : BasePresenterFragment<RoadsNearFMPresenter>(), RoadsNearFMView {

    override fun createPresenter(): RoadsNearFMPresenter = RoadsNearFMPresenter(this)

    private val data = ArrayList<RoadsMDL>()
    private lateinit var adapter: RoadsNearFmListAdapter
    private val handler = Handler()

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = RoadsNearFmListAdapter(context, data)
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter.getNewsList(WebApi.GET_ROADS_LIST, WebApi.getRoadListParams(1, 4, ""))
    }

    override fun onGetNewList(list: MutableList<RoadsMDL>) {
        data.clear()
        data.addAll(list)
        data.forEach {
            it.colors = ArrayList<Int>().apply {
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#ED1C24"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#F7F30A"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#ED1C24"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
            }
        }
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