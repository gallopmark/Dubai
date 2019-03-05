package com.uroad.dubai.activity

import android.os.Bundle
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.EventsPresenter
import com.uroad.dubai.api.presenter.RouteNavigationPresenter
import com.uroad.dubai.api.view.RouteNavigationView
import com.uroad.dubai.common.BaseMapBoxActivity
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.widget.AppCompatNavigationMapRoute
import com.uroad.library.utils.DisplayUtils

class RoadLineActivity : BaseMapBoxActivity(), RouteNavigationView, EventsPresenter.EventsView {
    private lateinit var presenter: RouteNavigationPresenter
    private var navigationMapRoute: AppCompatNavigationMapRoute? = null
    private var eventPresenter: EventsPresenter? = null
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null

    override fun setBaseMapBoxView(): Int = R.layout.activity_roadline

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        presenter = RouteNavigationPresenter(this, this)
        withTitle(intent.extras?.getString("title"))
    }

    override fun onMapAsync(mapBoxMap: MapboxMap, style: Style) {
        navigationMapRoute = AppCompatNavigationMapRoute(mapView, mapBoxMap).apply { isDrawWayPointMarkers(false) }
        val startPoint = intent.extras?.getString("startPoint")
        val endPoint = intent.extras?.getString("endPoint")
        if (!startPoint.isNullOrEmpty() && !endPoint.isNullOrEmpty()) {
            val start = Point.fromJson(startPoint)
            val end = Point.fromJson(endPoint)
            this.startPoint = LatLng(start.latitude(), start.longitude())
            this.endPoint = LatLng(end.latitude(), end.longitude())
            presenter.getRoutes(start, end, DirectionsCriteria.PROFILE_DRIVING)
        }
        val roadId = intent.extras?.getString("roadid")
        if (roadId.isNullOrEmpty()) {
            eventPresenter = EventsPresenter(this).apply { getEventList(0, 0, roadId, 0.0, 0.0) }
        }
    }

    override fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?) {
        if (routes.isNullOrEmpty()) {
            showShortToast(getString(R.string.no_routes_found))
        } else {
            navigationMapRoute?.addRoute(routes[0])
            zoomToSpan(startPoint, endPoint)
        }
    }

    private fun zoomToSpan(startPoint: LatLng?, endPoint: LatLng?) {
        val builder = LatLngBounds.Builder()
        startPoint?.let { builder.include(it) }
        endPoint?.let { builder.include(it) }
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), DisplayUtils.dip2px(this, 50f)))
    }

    override fun onGetEventList(data: MutableList<EventsMDL>) {

    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onHideLoading() {
        endLoading()
    }

    override fun onShowError(msg: String?) {
        showShortToast(msg)
    }

    override fun onDestroy() {
        presenter.detachView()
        eventPresenter?.detachView()
        super.onDestroy()
    }
}