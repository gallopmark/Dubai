package com.uroad.dubai.api.presenter

import android.app.Activity
import android.content.Context
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.DirectionsRouteAdapter
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.RouteNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteNavigationPresenter(private val context: Context,
                               private val naviView: RouteNavigationView)
    : BasePresenter<RouteNavigationView>(naviView) {

    private var directions: MapboxDirections? = null

    fun getRoutes(origin: Point, destination: Point, profile: String) {
        naviView.onShowLoading()
        directions = buildDirections(origin, destination, profile).apply {
            enqueueCall(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    naviView.onHideLoading()
                    naviView.onNavigationRoutes(response.body()?.routes())
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    naviView.onHideLoading()
                    naviView.onShowError(t.message)
                }
            })
        }
    }

    private fun buildDirections(origin: Point, destination: Point, profile: String): MapboxDirections {
        return MapboxDirections.builder()
                .profile(profile)
                .origin(origin)
                .destination(destination)
                .annotations(DirectionsCriteria.ANNOTATION_CONGESTION, DirectionsCriteria.ANNOTATION_DISTANCE)
                .accessToken(context.getString(R.string.mapBoxToken))
                .steps(true)
                .continueStraight(true)
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .alternatives(true)
                .roundaboutExits(true)
                .build()
    }

    fun getDirectionsRouteAdapter(context: Activity, routes: MutableList<DirectionsRoute>, onItemSelectedListener: DirectionsRouteAdapter.OnItemSelectedListener): DirectionsRouteAdapter {
        return DirectionsRouteAdapter(context, routes).apply { setOnItemSelectedListener(onItemSelectedListener) }
    }

    override fun detachView() {
        directions?.cancelCall()
        super.detachView()
    }
}