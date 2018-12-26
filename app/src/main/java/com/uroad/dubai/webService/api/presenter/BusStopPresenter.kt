package com.uroad.dubai.webService.api.presenter

import com.uroad.dubai.model.BusStopMDL
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.model.PoliceMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.api.BasePresenter
import com.uroad.dubai.webService.api.StringObserver
import com.uroad.dubai.webService.api.view.BusStopView
import com.uroad.dubai.webService.api.view.PoliceView

class BusStopPresenter(val busStopView: BusStopView) : BasePresenter<BusStopView>(busStopView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(busStopView) {
            override fun onHttpResultOk(data: String?) {
                busStopView.onGetNewList(GsonUtils.fromDataToList(data, BusStopMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                busStopView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}