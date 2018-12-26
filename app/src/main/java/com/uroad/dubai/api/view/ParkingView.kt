package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.ParkingMDL

interface ParkingView : BaseView {
    fun onGetNewList(list: MutableList<ParkingMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
