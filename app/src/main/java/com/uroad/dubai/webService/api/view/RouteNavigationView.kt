package com.uroad.dubai.webService.api.view

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.webService.api.BaseView

interface RouteNavigationView : BaseView {
    fun onPoiResult(features: MutableList<CarmenFeature>, type: Int)
    fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?)
}