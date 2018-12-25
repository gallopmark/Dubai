package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R
import com.uroad.dubai.enumeration.MapDataType

class EventsMDL : MultiItem {
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
            if (it == MapDataType.ACCIDENT.code) return R.mipmap.ic_event_accident
            if (it == MapDataType.CONSTRUCTION.code) return R.mipmap.ic_event_construction
        }
        return 0
    }

    fun getSmallMarkerIcon(): Int {
        subtype?.let {
            if (it == MapDataType.ACCIDENT.code) return R.mipmap.ic_marker_accident
            if (it == MapDataType.CONSTRUCTION.code) return R.mipmap.ic_marker_construction
        }
        return 0
    }

    fun getBigMarkerIcon(): Int {
        subtype?.let {
            if (it == MapDataType.ACCIDENT.code) return R.mipmap.ic_marker_accident_big
            if (it == MapDataType.CONSTRUCTION.code) return R.mipmap.ic_marker_construction_big
        }
        return 0
    }

    fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }

    override fun getItemType(): Int = 1
}