package com.uroad.dubai.model

import com.mapbox.geojson.Point
import java.lang.Exception

class RoadsMDL {
    /**
     * roadid	路段ID
    roadname	路段名称
    roadtype	路段类型
    miles	路段长度
    startpoint	起始点
    endpoint	结束点
    icon	图标链接
    startlatitudeandlongitude	开始点的经纬度
    endlatitudeandlongitude	结束点的经纬度
    eventdata	事件数组
     */
    var icon: String? = null
    var iconInt: Int = 0
    var title: String? = null
    var content: String? = null
    var distance: String? = null
    var endpoint: String? = null
    var startpoint: String? = null
    var miles: String? = null
    var roadtype: String? = null
    var roadname: String? = null
    var roadid: String? = null
    var startlatitudeandlongitude: String? = null
    var endlatitudeandlongitude: String? = null
    var eventdata: String? = null
    var colors: MutableList<Int>? = null

    fun getStartPoint(): Point? {
        startlatitudeandlongitude?.let {
            return try {
                val arr = it.split(",")
                Point.fromLngLat(arr[0].toDouble(), arr[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getEndPoint(): Point? {
        endlatitudeandlongitude?.let {
            return try {
                val arr = it.split(",")
                Point.fromLngLat(arr[0].toDouble(), arr[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getRoadColors(): MutableList<Int> {
        colors?.let { return it }
        return ArrayList()
    }
}