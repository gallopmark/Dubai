package com.uroad.dubai.api.presenter

import android.content.Context
import android.support.v4.util.ArrayMap
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SubscribePresenter(private val context: Context,
                         private val subscribeView: SubscribeView)
    : BasePresenter<SubscribeView>(subscribeView) {
    private var arrayMap = ArrayMap<Int, MapboxDirections>()
    fun getSubscribeData(userId: String?) {
        request(WebApi.GET_SUBSCRIBE_DATA, WebApi.simpleParams(userId), object : StringObserver(subscribeView) {
            override fun onHttpResultOk(data: String?) {
                subscribeView.onGetSubscribeData(GsonUtils.fromDataToList(data, SubscribeMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                subscribeView.onFailure(errorMsg, errorCode)
            }
        })
    }

    fun buildDirections(origin: Point, destination: Point, profile: String): MapboxDirections {
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

    fun requestRoutes(data: MutableList<SubscribeMDL>, callback: SubscribeRouteCallback) {
        arrayMap.clear()
        for (i in 0 until data.size) {
            val startPoint = data[i].getOriginPoint()
            val endPoint = data[i].getDestinationPoint()
            if (startPoint != null && endPoint != null) {
                arrayMap[i] = buildDirections(startPoint, endPoint, data[i].getProfile())
            }
        }
        requestRoutes(callback)
    }

    private fun requestRoutes(callback: SubscribeRouteCallback) {
        addDisposable(Observable.fromCallable {
            val map = ArrayMap<Int, DirectionsRoute>()
            for ((k, v) in arrayMap) {
                try {
                    val response = v.executeCall()
                    val directionsRoute = response.body()?.routes()?.get(0)
                    directionsRoute?.let { route -> map[k] = route }
                } catch (e: Exception) {
                    continue
                }
            }
            map
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            callback.getRoutes(it)
        }, { }))
    }

    interface SubscribeRouteCallback {
        fun getRoutes(map: ArrayMap<Int, DirectionsRoute>)
    }
}