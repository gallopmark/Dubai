package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class SubscribePresenter(private val subscribeView: SubscribeView)
    : BasePresenter<SubscribeView>(subscribeView) {

    fun getSubscribeData(userId: String?) {
        request(WebApi.GET_SUBSCRIBE_DATA, WebApi.simpleParams(userId), object : StringObserver(subscribeView) {
            override fun onHttpResultOk(data: String?) {
                subscribeView.onGetSubscribeData(GsonUtils.fromDataToList(data, SubscribeMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                subscribeView.onFailure(errorMsg, errorCode)
            }
        })
    }
}