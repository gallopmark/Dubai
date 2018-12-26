package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

class ParkingMDL {
    var id: String? = null
    var time: String? = null
    var title: String? = null
    var content: String? = null
    var address: String? = null
    var num: String? = null
    var distance: String? = null
    var imgPath: String? = null
    var type: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    var available: String? = null
    var total: String? = null

    fun getIcon(): Int = R.mipmap.ic_parking_round
    fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_parking

    fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_parking_big

    fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}