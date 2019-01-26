package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.VersionMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class AppVersionPresenter(private val versionView: AppVersionView)
    : BasePresenter<AppVersionPresenter.AppVersionView>(versionView) {

    fun checkVersion() {
        request(WebApi.APP_VERSION, WebApi.getBaseParams(), object : StringObserver(versionView) {
            override fun onHttpResultOk(data: String?) {
                versionView.getVersion(GsonUtils.fromDataBean(data,VersionMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                versionView.onShowError(errorMsg)
            }
        })
    }

    interface AppVersionView : BaseView {
        fun getVersion(mdl: VersionMDL?)
    }
}