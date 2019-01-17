package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.CalendarMDL
import java.util.ArrayList

interface CalendarView : BaseView {
    fun loadCalendarSuccess( list: ArrayList<CalendarMDL>)
    fun loadError(e:String)
    //fun onGetNewList(news:MutableList<CalendarMDL>)
    //fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}