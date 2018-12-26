package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.PoliceMDL

interface PoliceView : BaseView {
    fun onGetNewList(list: MutableList<PoliceMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
