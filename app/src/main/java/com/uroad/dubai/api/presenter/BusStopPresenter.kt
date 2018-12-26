package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.BusStopMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.view.BusStopView

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