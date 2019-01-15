package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.RoadsMDL
import com.uroad.library.utils.DisplayUtils

class RoadsListCardAdapter(private val context: Context, data: MutableList<RoadsMDL>)
    : BaseArrayRecyclerAdapter<RoadsMDL>(context, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_roadslist_card

    override fun onBindHoder(holder: RecyclerHolder, t: RoadsMDL, position: Int) {
        holder.displayImage(R.id.ivIcon, t.icon)
        holder.setText(R.id.tvTitle, t.roadname)
        holder.setText(R.id.tvContent, t.content)
        holder.setText(R.id.tvEventInfo, "Event Info")
        holder.setText(R.id.tvConstruction, "1")
        holder.setText(R.id.tvAccident, "3")
        holder.setText(R.id.tvDistance, t.distance)
        val recyclerView = holder.obtainView<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        recyclerView.adapter = RoadColorAdapter(context, t.getRoadColors())
    }

    private inner class RoadColorAdapter(context: Context, colors: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, colors) {
        private val totalWidth = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 16f) * 2 - DisplayUtils.dip2px(context, 10f) * 2
        private val measureSize = totalWidth / colors.size + totalWidth % colors.size
        override fun bindView(viewType: Int): Int = R.layout.item_nearmeroads_color

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply { width = measureSize }
            holder.setBackgroundColor(R.id.vColor, t)
        }
    }
}