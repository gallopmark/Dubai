package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng

abstract class MapPointItem {
    abstract fun getSmallMarkerIcon(): Int
    abstract fun getBigMarkerIcon(): Int
    abstract fun getLatLng(): LatLng
    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is MapPointItem -> false
            else -> this === other || (getLatLng().longitude == other.getLatLng().longitude
                    && getLatLng().latitude == other.getLatLng().latitude)
        }
    }

    override fun hashCode(): Int {
        return 31 + (getLatLng().hashCode())
    }
}