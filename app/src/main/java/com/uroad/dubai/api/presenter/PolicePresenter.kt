package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.PoliceMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.view.PoliceView

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