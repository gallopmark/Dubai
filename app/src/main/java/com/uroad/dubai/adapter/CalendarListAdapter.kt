package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.CalendarMDL


class CalendarListAdapter(private val context: Context, data: MutableList<CalendarMDL>)
      : BaseArrayRecyclerAdapter<CalendarMDL>(context,data) {

    override fun onBindHoder(holder: RecyclerHolder, t: CalendarMDL, position: Int) {
        holder.setText(R.id.tvWeekDateTitle,t.weekDataTitle)
        val recyclerView = holder.obtainView<RecyclerView>(R.id.ryCalendarContent)
        val layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = CalendarListContentAdapter(context, t.list!!)
    }



    override fun bindView(viewType: Int): Int = R.layout.item_calendar
}