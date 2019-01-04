package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.ScenicMDL

interface MainBannerView :BaseView{
    fun onGetBanner(list: MutableList<ScenicMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}