package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng

class WeatherMDL : MapPointItem {
    var temperature: String? = null
    var weather: String? = null
    var city: String? = null
    var Icon: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    override fun getSmallMarkerIcon(): Int = 0

    override fun getBigMarkerIcon(): Int = 0

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}