package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng

interface MapPointItem {
    fun getSmallMarkerIcon(): Int
    fun getBigMarkerIcon(): Int
    fun getLatLng(): LatLng
}