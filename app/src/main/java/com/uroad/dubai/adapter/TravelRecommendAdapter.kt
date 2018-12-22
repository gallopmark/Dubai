package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.model.RecommendMDL
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter

class TravelRecommendAdapter(context: Context, data: MutableList<RecommendMDL>)
    : BannerBaseArrayAdapter<RecommendMDL>(context, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_travel_recommend

    override fun bindHolder(holder: RecyclerHolder, t: RecommendMDL, position: Int) {
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvContent, t.content)
        holder.setText(R.id.tvAddress, t.address)
        holder.setText(R.id.tvDistance, t.distance)
    }
}