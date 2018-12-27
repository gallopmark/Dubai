package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.AttractionNearFMMDL

interface AttractionNearFMView : BaseView {
    fun onGetNewList(list: MutableList<AttractionNearFMMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
