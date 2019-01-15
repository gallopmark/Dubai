package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.EventsMDL

class EventsListCardAdapter(context: Context, data: MutableList<EventsMDL>)
    : BaseArrayRecyclerAdapter<EventsMDL>(context, data) {

    override fun bindView(viewType: Int): Int = R.layout.item_eventslist_card

    override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
        holder.setImageResource(R.id.ivIcon, t.iconInt)
        holder.setText(R.id.tvTitle, t.roadtitle)
        holder.setText(R.id.tvTime, t.updatetime)
        holder.setText(R.id.tvContent,t.reportout)
    }
}