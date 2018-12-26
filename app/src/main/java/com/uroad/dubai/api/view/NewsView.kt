package com.uroad.dubai.api.view

import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.api.BaseView

interface NewsView : BaseView {
    fun onGetNewList(news:MutableList<NewsMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}