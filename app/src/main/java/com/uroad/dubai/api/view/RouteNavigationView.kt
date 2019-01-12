package com.uroad.dubai.api.view

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.api.BaseView

interface RouteNavigationView : PoiSearchView {
    fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?)
}