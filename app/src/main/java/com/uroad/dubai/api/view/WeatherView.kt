package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.WeatherMDL

interface WeatherView : BaseView {
    fun onGetNewList(mdl : WeatherMDL?)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}