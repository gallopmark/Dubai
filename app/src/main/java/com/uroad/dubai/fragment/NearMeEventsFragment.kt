package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.EventsListCardAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.model.EventsMDL
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeEventsFragment : BaseFragment() {

    private val data = ArrayList<EventsMDL>()
    private lateinit var cardAdapter: EventsListCardAdapter
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        cardAdapter = EventsListCardAdapter(context, data)
        recyclerView.adapter = cardAdapter
    }

    override fun initData() {
        data.add(getMDL(0))
        data.add(getMDL(1))
        data.add(getMDL(2))
        data.add(getMDL(3))
        cardAdapter.notifyDataSetChanged()
    }

    private fun getMDL(position: Int): EventsMDL {
        return EventsMDL().apply {
            iconInt = when (position) {
                0 -> R.mipmap.ic_roads_e11
                1 -> R.mipmap.ic_roads_e44
                2 -> R.mipmap.ic_roads_e311
                else -> R.mipmap.ic_roads_e66
            }
            updatetime = when(position){
                0 -> "58 mins ago"
                1 -> "1h ago"
                2 -> "12/11 14:22"
                else -> "1/15 16:17"
            }
            roadtitle = "Dubai to ABU dhabi"
            reportout = "On the morning of 6 February, 22 people were injured in a major road accident along sheikh M-ohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
        }
    }
}