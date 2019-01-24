package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.CalendarListMDL


class CalendarListContentAdapter(private val context: Context, data: MutableList<CalendarListMDL>)
      : BaseArrayRecyclerAdapter<CalendarListMDL>(context,data) {

    override fun onBindHoder(holder: RecyclerHolder, t: CalendarListMDL, position: Int) {
        holder.setText(R.id.tvCalConTitle,t.title)
        holder.setText(R.id.tvCalConTime,getStr(t.dtstart,t.dtend))
        if (position != 0 && position == mDatas.size-1)
            holder.setVisibility(R.id.tvCalConView,false)
    }

    override fun bindView(viewType: Int): Int = R.layout.item_calendar_content

    private fun getStr(dtstart: String?, dtend: String?):String{
        return "${dtstart?.substring(10,dtstart.length-3)}  -  ${dtend?.substring(10,dtend.length-3)}".trim()
    }
}