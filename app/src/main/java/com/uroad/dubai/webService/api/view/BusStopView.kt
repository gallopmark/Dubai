package com.uroad.dubai.webService.api.view

import com.uroad.dubai.model.BusStopMDL
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.model.PoliceMDL
import com.uroad.dubai.webService.api.BaseView

interface BusStopView : BaseView{
    fun onGetNewList(list: MutableList<BusStopMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
