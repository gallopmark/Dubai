package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

class ScenicMDL : NewsMDL(), MapPointItem {
    var name: String? = null
    var picurls: String? = null
    var detailurl: String? = null
    var distance: String? = null
    override fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_scenic

    override fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_scenic_big

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }

}