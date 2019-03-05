package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

class PoliceMDL : MapPointItem() {

    var id: String? = null
    var time: String? = null
    var title: String? = null
    var content: String? = null
    var address: String? = null
    var type: String? = null
    var telphone: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    override fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_police

    override fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_police_big

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}