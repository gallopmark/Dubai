package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.NewsMDL

interface NewsDetailView : BaseView {
    fun onGetNews(newsMDL: NewsMDL)
    fun onParseError()
    fun onFailure(errorMsg: String?, errorCode: Int?)
}