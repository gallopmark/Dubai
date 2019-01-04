package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.ScenicMDL

interface RoadNavigationView : BaseView {
    fun onGetScenic(data: MutableList<ScenicMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}