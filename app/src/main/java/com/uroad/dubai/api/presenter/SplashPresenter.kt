package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.StartupMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class SplashPresenter(private val splashView: SplashView)
    : BasePresenter<SplashPresenter.SplashView>(splashView) {

    fun getStartupImage() {
        request(WebApi.STARTUP_IMAGE, WebApi.getBaseParams(), object : StringObserver(splashView) {
            override fun onHttpResultOk(data: String?) {
                splashView.onSplashResponse(GsonUtils.fromDataBean(data, StartupMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                splashView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }

    interface SplashView : BaseView {
        fun onSplashResponse(mdl: StartupMDL?)
        fun onHttpResultError(errorMsg: String?, errorCode: Int?)
    }
}