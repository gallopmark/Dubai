package com.uroad.dubai.model

class PoiSearchTextMDL : MultiItem {
    override fun getItemType(): Int = 1
    var content: String? = null
}