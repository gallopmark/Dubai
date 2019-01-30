package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.NewsMDL

interface AttractionNearFMView : BaseView {
    fun onGetNewList(list: MutableList<NewsMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
