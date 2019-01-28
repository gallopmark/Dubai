package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.EventsMDL

class EventsListCardAdapter(context: Context, data: MutableList<EventsMDL>)
    : BaseArrayRecyclerAdapter<EventsMDL>(context, data) {

    override fun bindView(viewType: Int): Int = R.layout.item_eventslist_card

    override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
        holder.displayImage(R.id.ivIcon, t.icon, R.color.color_f7)
        holder.setText(R.id.tvTitle, t.roadname)
        holder.setText(R.id.mDirectionTv, t.direction)
        holder.setText(R.id.tvContent, t.eventinfo)
    }
}