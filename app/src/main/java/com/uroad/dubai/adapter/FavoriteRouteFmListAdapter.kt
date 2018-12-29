package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.RouteMDL
import com.uroad.library.utils.DisplayUtils

class FavoriteRouteFmListAdapter(private val context: Context, data: MutableList<RouteMDL>)
      : BaseArrayRecyclerAdapter<RouteMDL>(context,data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 3
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    override fun bindView(viewType: Int): Int = R.layout.item_favorites_route

    override fun onBindHoder(holder: RecyclerHolder, t: RouteMDL, position: Int) {
        //holder.setLayoutParams(R.id.ivPic, params)
        //holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
        val recyclerView = holder.obtainView<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        recyclerView.adapter = RoadColorAdapter(context, t.getRoadColors())
    }

    private inner class RoadColorAdapter(context: Context, colors: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, colors) {
        private val totalWidth = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 10f) * 4
        private val measureSize = totalWidth / colors.size + totalWidth % colors.size
        override fun bindView(viewType: Int): Int = R.layout.item_nearmeroads_color

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply { width = measureSize }
            holder.setBackgroundColor(R.id.vColor, t)
        }
    }
}