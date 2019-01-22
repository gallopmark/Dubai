package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.SubscribeMDL

interface SubscribeView : BaseView {
    fun onGetSubscribeData(data: MutableList<SubscribeMDL>)
    fun onFailure(errMsg: String?, errCode: Int?)
}