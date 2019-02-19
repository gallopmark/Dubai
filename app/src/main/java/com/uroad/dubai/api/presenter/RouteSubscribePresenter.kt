package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.RouteSubscribeView
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

/**
 * @author MFB
 * @create 2019/1/22
 * @describe subscribe route
 */
class RouteSubscribePresenter(private val subscribeView: RouteSubscribeView)
    : BasePresenter<RouteSubscribeView>(subscribeView) {

    fun subscribeRoute(params: HashMap<String, String?>) {
        request(WebApi.SUBSCRIBE_ROUTE, params, object : StringObserver(subscribeView) {
            override fun onHttpResultOk(data: String?) {
                subscribeView.onSuccess(GsonUtils.fromDataBean(data, SubscribeMDL::class.java)?.routeid, true)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                subscribeView.onFailure(errorMsg, errorCode)
            }
        })
    }

    fun unSubscribeRoute(useruuid: String?, routeId: String?, type: String?) {
        request(WebApi.UNSUBSCRIBE_ROUTE, WebApi.unSubscribeRouteParams(useruuid, routeId, type), object : StringObserver(subscribeView) {
            override fun onHttpResultOk(data: String?) {
                subscribeView.onSuccess(GsonUtils.getDataAsString(data), false)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                subscribeView.onFailure(errorMsg, errorCode)
            }
        })
    }
}