package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.RoadsMDL

interface RoadsNearFMView : BaseView {
    fun onGetNewList(list: MutableList<RoadsMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
