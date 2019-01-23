package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.BannerView
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class BannerPresenter(private val bannerView: BannerView)
    : BasePresenter<BannerView>(bannerView) {

    fun getBannerNews(bannerType: String?) {
        request(WebApi.BANNER_NEWS, WebApi.bannerNewsParams(bannerType), object : StringObserver(bannerView) {
            override fun onHttpResultOk(data: String?) {
                bannerView.onGetNews(GsonUtils.fromDataToList(data, NewsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                bannerView.onFailure(errorMsg, errorCode)
            }
        })
    }
}