package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.BusStopMDL

interface BusStopView : BaseView {
    fun onGetNewList(list: MutableList<BusStopMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
