package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.MainBannerView
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class MainBannerPresenter(private val bannerView: MainBannerView)
    : BasePresenter<MainBannerView>(bannerView) {
    fun getBanner() {
        request(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4), object : StringObserver(bannerView) {
            override fun onHttpResultOk(data: String?) {
                bannerView.onGetBanner(GsonUtils.fromDataToList(data, ScenicMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                bannerView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}