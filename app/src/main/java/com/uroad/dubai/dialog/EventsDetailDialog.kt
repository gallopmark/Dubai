package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL

/**
 * @author MFB
 * @create 2018/12/19
 * @describe events detail dialog
 */
class EventsDetailDialog : BaseMapPointDialog<EventsMDL> {
    constructor(context: Context, mdl: EventsMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<EventsMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = EventDetailAdapter(mContext, data).apply {
            setOnItemChildClickListener(object : BaseRecyclerAdapter.OnItemChildClickListener {
                override fun onItemChildClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (view.id == R.id.ivClose) dismiss()
                }
            })
        }
    }

    private inner class EventDetailAdapter(context: Context, data: MutableList<EventsMDL>)
        : BaseArrayRecyclerAdapter<EventsMDL>(context, data) {
        override fun bindView(viewType: Int): Int = R.layout.item_eventsdetail

        override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
            holder.setImageResource(R.id.ivIcon, t.getIcon())
            holder.setText(R.id.tvTitle, t.eventtype)
            holder.setText(R.id.tvTime, "")
            holder.setText(R.id.tvContent, t.roadname)
            holder.setText(R.id.tvStatus, t.congtstatus)
            holder.setText(R.id.tvDetail, t.eventinfo)
            holder.setText(R.id.tvStartTime, t.starttime)
            holder.setText(R.id.tvHandleTime, t.handldtime)
            holder.setText(R.id.tvEndTime, t.endtime)
            holder.bindChildClick(R.id.ivClose)
        }
    }
}