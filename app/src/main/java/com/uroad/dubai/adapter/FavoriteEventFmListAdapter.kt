package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.model.RouteMDL
import com.uroad.library.utils.DisplayUtils

class FavoriteEventFmListAdapter(context: Context, data: MutableList<EventsMDL>)
    : BaseArrayRecyclerAdapter<EventsMDL>(context, data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 3
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    override fun bindView(viewType: Int): Int = R.layout.item_favorites_event

    override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
        //holder.setLayoutParams(R.id.ivFavEventType, params)
        holder.displayImage(R.id.ivFavEventType, t.icon,R.color.color_f7)
        holder.setText(R.id.tvFavEventName, t.roadname)
        holder.setText(R.id.tvFavEventTime, "")
        holder.setText(R.id.tvFavEventContent, t.eventinfo)
        //holder.displayImage(R.id.ivFavEventType, t.iconInt, R.color.color_f7, RoundedCorners(dp4))
    }

}