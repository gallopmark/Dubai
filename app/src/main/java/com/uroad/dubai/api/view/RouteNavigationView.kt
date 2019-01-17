package com.uroad.dubai.api.view

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.uroad.dubai.api.BaseView

interface RouteNavigationView : BaseView {
    fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?)
}