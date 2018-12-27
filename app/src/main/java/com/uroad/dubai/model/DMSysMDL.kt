package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

class DMSysMDL : MapPointItem {
    var id: String? = null
    var title: String? = null
    var content: String? = null
    var picurl: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    override fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_dms

    override fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_dms_big

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}