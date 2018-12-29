package com.uroad.dubai.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.NearMeRoadsAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.model.RoadsMDL
import kotlinx.android.synthetic.main.fragment_nearmeroads.*

class NearMeRoadsFragment : BaseFragment() {

    private val data = ArrayList<RoadsMDL>()
    private lateinit var adapter: NearMeRoadsAdapter
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_nearmeroads)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = NearMeRoadsAdapter(context, data)
        recyclerView.adapter = adapter
    }

    override fun initData() {
        data.add(getMDL(0))
        data.add(getMDL(1))
        data.add(getMDL(2))
        data.add(getMDL(3))
        adapter.notifyDataSetChanged()
    }

    private fun getMDL(position: Int): RoadsMDL {
        return RoadsMDL().apply {
            iconInt = when (position) {
                0 -> R.mipmap.ic_roads_e11
                1 -> R.mipmap.ic_roads_e44
                2 -> R.mipmap.ic_roads_e311
                else -> R.mipmap.ic_roads_e66
            }
            title = "sheikh Mohammed bin rashid al-" +
                    "maktoum road (E311)"
            content = "Dubai to ABU dhabi"
            distance = when(position){
                0 -> "1.7 km"
                1 -> "2.1 km"
                2 -> "3.2 km"
                else -> "5.6 km"
            }
            colors = ArrayList<Int>().apply {
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#ED1C24"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#F7F30A"))
                add(Color.parseColor("#ED1C24"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
                add(Color.parseColor("#06A72B"))
            }
        }
    }
}