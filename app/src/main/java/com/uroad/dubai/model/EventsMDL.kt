package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R
import com.uroad.dubai.enumeration.MapDataType

/**
 * eventid	事件ID
eventinfo	事件描述
eventtype	事件类型
locdetail	事件发生地
direction	方向
longitude	经度
latitude	维度
congtstatus	拥堵状态
congtstarttime	拥堵开始时间
congtendtime	拥堵结束时间
handldtime	处理时间
roadname	路名
congestiondistance	拥堵距离	单位 km
starttime	开始时间
endtime	结束时间
 */
class EventsMDL : MapPointItem {
    var iconInt: Int = 0
    var subscribestatus: Int? = 0
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

    var eventid: String? = null
    var eventinfo: String? = null
    var eventtype: String? = null
    var locdetail: String? = null
    var direction: String? = null
    var longitude: Double? = 0.0
    var latitude: Double? = 0.0
    var congtstatus: String? = null //拥堵状态
    var congtstarttime: String? = null //拥堵开始时间
    var congtendtime: String? = null //拥堵结束时间
    var handldtime: String? = null //处理时间
    var roadname: String? = null //路名
    var congestiondistance: String? = null //拥堵距离	单位 km
    var starttime: String? = null //开始时间
    var endtime: String? = null //结束时间

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