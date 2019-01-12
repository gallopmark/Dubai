package com.uroad.dubai.api.view

import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.api.BaseView

interface PoiSearchView : BaseView {
    fun onPoiResult(features: MutableList<CarmenFeature>)
}