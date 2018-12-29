package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.RouteMDL

interface FavoriteRouteFMView : BaseView {
    fun onGetNewList(list: MutableList<RouteMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
