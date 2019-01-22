package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter

class MainSubscribeAdapter(context: Context, data: MutableList<SubscribeMDL>)
    : BannerBaseArrayAdapter<SubscribeMDL>(context, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_subscribe_route

    override fun bindHolder(holder: RecyclerHolder, t: SubscribeMDL, position: Int) {
        holder.setText(R.id.tvSubscribeName, "Route")
        holder.setText(R.id.tvRouteArrow, t.getStartEndPoint())
        holder.setText(R.id.tvDistance, t.distance)
        holder.setText(R.id.tvTravelTime, t.travelTime)
    }
}