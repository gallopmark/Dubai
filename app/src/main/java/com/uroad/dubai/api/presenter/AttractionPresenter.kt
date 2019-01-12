package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.AttractionView
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.utils.GsonUtils

class AttractionPresenter(private val attractionView: AttractionView)
    : BasePresenter<AttractionView>(attractionView) {

    fun getAttractions(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(attractionView) {
            override fun onHttpResultOk(data: String?) {
                attractionView.onGetAttraction(GsonUtils.fromDataToList(data, ScenicMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                attractionView.onShowError(errorMsg)
            }
        })
    }
}