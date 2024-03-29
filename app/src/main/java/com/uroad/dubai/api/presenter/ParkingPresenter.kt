package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.ParkingMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.view.ParkingView

class ParkingPresenter(val parkingView: ParkingView) : BasePresenter<ParkingView>(parkingView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(parkingView) {
            override fun onHttpResultOk(data: String?) {
                parkingView.onGetNewList(GsonUtils.fromDataToList(data, ParkingMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                parkingView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}