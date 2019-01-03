package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.CalendarMDL

interface CalendarView : BaseView {
    fun onGetNewList(news:MutableList<CalendarMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}