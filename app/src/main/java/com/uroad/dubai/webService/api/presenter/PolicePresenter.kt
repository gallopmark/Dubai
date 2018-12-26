package com.uroad.dubai.webService.api.presenter

import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.model.PoliceMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.api.BasePresenter
import com.uroad.dubai.webService.api.StringObserver
import com.uroad.dubai.webService.api.view.PoliceView

class PolicePresenter(val policeView: PoliceView) : BasePresenter<PoliceView>(policeView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(policeView) {
            override fun onHttpResultOk(data: String?) {
                policeView.onGetNewList(GsonUtils.fromDataToList(data, PoliceMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                policeView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}