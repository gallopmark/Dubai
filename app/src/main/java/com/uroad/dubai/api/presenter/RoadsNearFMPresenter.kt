package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.RoadsNearFMView
import com.uroad.dubai.model.RoadsMDL
import com.uroad.dubai.utils.GsonUtils

class RoadsNearFMPresenter(val roadsNearFMView: RoadsNearFMView) : BasePresenter<RoadsNearFMView>(roadsNearFMView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(roadsNearFMView) {
            override fun onHttpResultOk(data: String?) {
                roadsNearFMView.onGetNewList(GsonUtils.fromDataToList(data, RoadsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                roadsNearFMView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}