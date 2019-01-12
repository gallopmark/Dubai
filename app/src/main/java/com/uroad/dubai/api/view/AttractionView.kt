package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.ScenicMDL

interface AttractionView : BaseView {
    fun onGetAttraction(attractions: MutableList<ScenicMDL>)
}