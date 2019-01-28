package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class EventsPresenter(private val eventsView: EventsView)
    : BasePresenter<EventsPresenter.EventsView>(eventsView) {

    fun getEventList(index: Int, size: Int, roadId: String?, longitude: Double, latitude: Double) {
        request(WebApi.EVENT_LIST, WebApi.eventListParams(index, size, roadId, longitude, latitude), object : StringObserver(eventsView) {
            override fun onHttpResultOk(data: String?) {
                eventsView.onGetEventList(GsonUtils.fromDataToList(data, EventsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                eventsView.onShowError(errorMsg)
            }
        })
    }

    interface EventsView : BaseView {
        fun onGetEventList(data: MutableList<EventsMDL>)
    }
}