package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.EventsMDL

interface FavoriteEventFMView : BaseView {
    fun onGetNewList(list: MutableList<EventsMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
