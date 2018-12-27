package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.AttractionNearFMView
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.model.AttractionNearFMMDL

class AttractionNearFMPresenter(val attractionNearView: AttractionNearFMView) : BasePresenter<AttractionNearFMView>(attractionNearView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(attractionNearView) {
            override fun onHttpResultOk(data: String?) {
                attractionNearView.onGetNewList(GsonUtils.fromDataToList(data, AttractionNearFMMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                attractionNearView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}