package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

/**
 * @author MFB
 * @create 2018/12/26
 * @describe
 */
class RWISMDL : MapPointItem() {

    var id: String? = null
    var title: String? = null
    var content: String? = null
    var reporttime: String? = null
    var windspeed: String? = null
    var visibility: String? = null
    var skyconditions: String? = null
    var temperature: String? = null
    var dewpoint: String? = null
    var relativehumidity: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    override fun getSmallMarkerIcon(): Int = R.mipmap.ic_marker_rwis

    override fun getBigMarkerIcon(): Int = R.mipmap.ic_marker_rwis_big

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}