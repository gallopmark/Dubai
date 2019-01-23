package com.uroad.dubai.model

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.geojson.Point
import com.uroad.dubai.R

class SubscribeMDL {
    var routeid: String? = null
    var startpoint: String? = null
    var endpoint: String? = null
    var startlatitudeandlongitude: String? = null
    var endlatitudeandlongitude: String? = null
    var plancode: String? = null
    var type: String? = null

    var distance: Double? = null
    var travelTime: Double? = null
    var congestion: MutableList<String>? = null

    fun getStartEndPoint(): String {
        var source = ""
        startpoint?.let { source += "${it}—" }
        endpoint?.let { source += it }
        return source
    }

    fun getOriginPoint(): Point? {
        startlatitudeandlongitude?.let {
            return try {
                val arr = it.split(",")
                Point.fromLngLat(arr[0].toDouble(), arr[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getDestinationPoint(): Point? {
        endlatitudeandlongitude?.let {
            return try {
                val arr = it.split(",")
                Point.fromLngLat(arr[0].toDouble(), arr[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getProfile(): String {
        return when {
            TextUtils.equals(plancode, "1") -> DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
            TextUtils.equals(plancode, "2") -> DirectionsCriteria.PROFILE_CYCLING
            TextUtils.equals(plancode, "3") -> DirectionsCriteria.PROFILE_WALKING
            else -> DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
        }
    }

    fun getRouteColors(context: Context): MutableList<Int> {
        val colorList = ArrayList<Int>()
        congestion?.let {
            for (item in it) {
                val color = if (TextUtils.equals(item, "moderate")) {
                    ContextCompat.getColor(context, R.color.routeModerateCongestionColor)
                } else if (TextUtils.equals(item, "heavy") || TextUtils.equals(item, "severe")) {
                    ContextCompat.getColor(context, R.color.routeSevereCongestionColor)
                } else {
                    ContextCompat.getColor(context, R.color.routeColor)
                }
                colorList.add(color)
            }
        }
        return colorList
    }
}