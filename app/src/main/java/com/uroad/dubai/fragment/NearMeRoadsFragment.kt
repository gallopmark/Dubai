package com.uroad.dubai.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.RoadLineActivity
import com.uroad.dubai.adapter.RoadsListCardAdapter
import com.uroad.dubai.api.presenter.RoadsNearFMPresenter
import com.uroad.dubai.api.view.RoadsNearFMView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.RoadsMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeRoadsFragment : NearMeBaseFragment<RoadsNearFMPresenter>(), RoadsNearFMView {

    companion object {
        fun newInstance(longitude: Double, latitude: Double): NearMeRoadsFragment {
            return NearMeRoadsFragment().apply {
                arguments = Bundle().apply {
                    putDouble("longitude", longitude)
                    putDouble("latitude", latitude)
                }
            }
        }
    }

    override fun createPresenter(): RoadsNearFMPresenter = RoadsNearFMPresenter(this)

    private val data = ArrayList<RoadsMDL>()
    private lateinit var adapter: RoadsListCardAdapter
    private val handler = Handler()

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initBundle()
        initRv()
    }

    private fun initBundle() {
        arguments?.let {
            longitude = it.getDouble("longitude", 0.0)
            latitude = it.getDouble("latitude", 0.0)
        }
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = RoadsListCardAdapter(context, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position !in 0 until data.size) return
                    openActivity(RoadLineActivity::class.java, Bundle().apply {
                        putString("title", data[position].roadname)
                        putString("startPoint", data[position].getStartPoint()?.toJson())
                        putString("endPoint", data[position].getEndPoint()?.toJson())
                    })
                }
            })
        }
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter.getNewsList(WebApi.GET_ROADS_LIST, WebApi.getRoadListParams(1, 4, "", longitude, latitude))
    }

    override fun onGetNewList(list: MutableList<RoadsMDL>) {
        data.clear()
        data.addAll(list)
        data.forEach {
            it.content = "Dubai to ABU dhabi"
            it.distance = "3.2km"
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