package com.uroad.dubai.model

import com.uroad.dubai.R

class ScenicMDL : NewsMDL() {
    var name: String? = null
    var picurls: String? = null
    var detailurl: String? = null

    override fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_scenic

    override fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_scenic_big
}