package com.uroad.dubai.model

import com.mapbox.api.geocoding.v5.models.CarmenFeature

class PoiSearchPoiMDL : MultiItem {
    override fun getItemType(): Int = 2
    var carmenFeature: CarmenFeature? = null
}