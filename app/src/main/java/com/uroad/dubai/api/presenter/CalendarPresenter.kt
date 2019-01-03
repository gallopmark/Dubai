package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.CalendarMDL

class CalendarPresenter(val calendarView: CalendarView) : BasePresenter<CalendarView>(calendarView) {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(calendarView) {
            override fun onHttpResultOk(data: String?) {
                calendarView.onGetNewList(GsonUtils.fromDataToList(data, CalendarMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                calendarView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}