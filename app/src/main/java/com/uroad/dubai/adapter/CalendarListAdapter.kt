package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.CalendarMDL


class CalendarListAdapter(private val context: Context, data: MutableList<CalendarMDL>)
      : BaseArrayRecyclerAdapter<CalendarMDL>(context,data) {

    override fun onBindHoder(holder: RecyclerHolder, t: CalendarMDL, position: Int) {

    }

    override fun bindView(viewType: Int): Int = R.layout.item_calendar
}