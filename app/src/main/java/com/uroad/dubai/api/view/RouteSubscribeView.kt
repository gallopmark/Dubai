package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView

interface RouteSubscribeView : BaseView {
    fun onSuccess()
    fun onFailure(errMsg: String?, errCode: Int?)
}