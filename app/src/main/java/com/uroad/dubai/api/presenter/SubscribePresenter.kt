package com.uroad.dubai.api.presenter

import android.content.Context
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class SubscribePresenter(private val context: Context,
                         private val subscribeView: SubscribeView)
    : BasePresenter<SubscribeView>(subscribeView) {

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
}