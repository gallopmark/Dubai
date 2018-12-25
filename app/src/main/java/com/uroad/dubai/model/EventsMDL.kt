package com.uroad.dubai.model

import com.uroad.dubai.R
import com.uroad.dubai.enumeration.MapDataType

class EventsMDL {
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
            if (it == MapDataType.CONSTRUCTION.code) return R.mipmap.ic_construction_round
        }
        return 0
    }
}