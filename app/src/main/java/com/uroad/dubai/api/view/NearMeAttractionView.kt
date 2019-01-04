package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.ScenicMDL

interface NearMeAttractionView : BaseView {
    fun onGetAttraction(attractions: MutableList<ScenicMDL>)
}