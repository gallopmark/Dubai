package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R
import com.uroad.dubai.enumeration.MapDataType

class EventsMDL : MapPointItem {
    var iconInt: Int = 0
    var subscribestatus: Int? = 0
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    var eventid: String? = null
    var eventtype: String? = null
    var subtype: String? = null
    var eventtypename: String? = null
    var roadtitle: String? = null
    var reportout: String? = null
    var occtime: String? = null
    var handletime: String? = null
    var realovertime: String? = null
    var planovertime: String? = null
    var statusname: String? = null
    var statuscolor: String? = null
    var updatetime: String? = null
    var isuseful: Int? = 0

    fun getIcon(): Int {
        subtype?.let {
            if (it == MapDataType.ACCIDENT.CODE) return R.mipmap.ic_accident_round
            if (it == MapDataType.CONSTRUCTION.CODE) return R.mipmap.ic_construction_round
        }
        return 0
    }

    override fun getSmallMarkerIcon(): Int {
        subtype?.let {
            if (it == MapDataType.ACCIDENT.CODE) return R.mipmap.ic_marker_accident
            if (it == MapDataType.CONSTRUCTION.CODE) return R.mipmap.ic_marker_construction
        }
        return 0
    }

    override fun getBigMarkerIcon(): Int {
        subtype?.let {
            if (it == MapDataType.ACCIDENT.CODE) return R.mipmap.ic_marker_accident_big
            if (it == MapDataType.CONSTRUCTION.CODE) return R.mipmap.ic_marker_construction_big
        }
        return 0
    }

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }
}