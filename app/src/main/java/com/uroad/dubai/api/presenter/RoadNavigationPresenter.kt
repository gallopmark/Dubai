package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class RoadNavigationPresenter(private val navigationView: RoadNavigationView)
    : BasePresenter<RoadNavigationView>(navigationView) {

    fun getScenic() {
        request(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 10), object : StringObserver(navigationView) {
            override fun onHttpResultOk(data: String?) {
                navigationView.onGetScenic(GsonUtils.fromDataToList(data, ScenicMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                navigationView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}