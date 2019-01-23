package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.NewsMDL

interface BannerView : BaseView {
    fun onGetNews(data: MutableList<NewsMDL>)
    fun onFailure(errorMsg: String?, errorCode: Int?)
}