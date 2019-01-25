package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView

interface RouteSubscribeView : BaseView {
    fun onSuccess(data: String?, isSubscribe: Boolean)
    fun onFailure(errMsg: String?, errCode: Int?)
}