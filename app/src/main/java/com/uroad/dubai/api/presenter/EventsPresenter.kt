package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class EventsPresenter() : BasePresenter<EventsPresenter.EventsView>() {
    private var eventsView: EventsView? = null

    constructor(eventsView: EventsView?) : this() {
        this.eventsView = eventsView
    }

    fun getEventList(index: Int, size: Int, roadId: String?, longitude: Double, latitude: Double) {
        request(WebApi.EVENT_LIST, WebApi.eventListParams(index, size, roadId, longitude, latitude), object : StringObserver(eventsView) {
            override fun onHttpResultOk(data: String?) {
                eventsView?.onGetEventList(GsonUtils.fromDataToList(data, EventsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                eventsView?.onShowError(errorMsg)
            }
        })
    }

    fun getEventDetails(eventId: String?, useruuid: String?, callback: EventDetailView) {
        request(WebApi.EVENT_DETAILS, WebApi.eventDetailsParams(eventId, useruuid), object : StringObserver(callback) {
            override fun onHttpResultOk(data: String?) {
                val mdl = GsonUtils.fromDataBean(data, EventsMDL::class.java)
                if (mdl == null) callback.onShowError("Data parsing exception")
                else callback.onGetEventDetail(mdl)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                callback.onShowError(errorMsg)
            }
        })
    }

    interface EventsView : BaseView {
        fun onGetEventList(data: MutableList<EventsMDL>)
    }

    interface EventDetailView : BaseView {
        fun onGetEventDetail(eventMDL: EventsMDL)
    }
}