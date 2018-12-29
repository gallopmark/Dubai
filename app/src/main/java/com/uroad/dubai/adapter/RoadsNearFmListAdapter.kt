package com.uroad.dubai.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.activity.DetailsActivity
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.AttractionNearFMMDL
import com.uroad.dubai.model.ParkingMDL
import com.uroad.dubai.model.RoadsMDL
import com.uroad.library.utils.DisplayUtils

class RoadsNearFmListAdapter(private val context: Context, data: MutableList<RoadsMDL>)
      : BaseArrayRecyclerAdapter<RoadsMDL>(context,data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 10
    private val imageHeight = imageWith * 5 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)

    override fun bindView(viewType: Int): Int = R.layout.item_roads_near

    override fun onBindHoder(holder: RecyclerHolder, t: RoadsMDL, position: Int) {
        Glide.with(context).load(t.icon).into(holder.obtainView(R.id.ivRoadsType))
        //holder.displayImage(R.id.ivRoadsType,t.icon,dp4)
        holder.setText(R.id.tvRoadsTitle,t.roadname)

        val recyclerView = holder.obtainView<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        recyclerView.adapter = RoadColorAdapter(context, t.getRoadColors())
    }

    private inner class RoadColorAdapter(context: Context, colors: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, colors) {
        private val totalWidth = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 40f)
        private val measureSize = totalWidth / colors.size + totalWidth % colors.size

        override fun bindView(viewType: Int): Int = R.layout.item_nearmeroads_color

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply { width = measureSize }
            holder.setBackgroundColor(R.id.vColor, t)
        }
    }

}