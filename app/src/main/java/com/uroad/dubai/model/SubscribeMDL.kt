package com.uroad.dubai.model

class SubscribeMDL {
    var routeid: String? = null
    var startpoint: String? = null
    var endpoint: String? = null
    var startlatitudeandlongitude: String? = null
    var endlatitudeandlongitude: String? = null
    var plancode: String? = null
    var type: String? = null

    var distance: String? = null
    var travelTime: String? = null
    fun getStartEndPoint(): String {
        var source = ""
        startpoint?.let { source += "${it}—" }
        endpoint?.let { source += it }
        return source
    }
}