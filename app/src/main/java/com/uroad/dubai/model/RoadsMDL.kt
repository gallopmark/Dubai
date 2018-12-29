package com.uroad.dubai.model

class RoadsMDL {
    var icon : String? = null
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
    var colors: MutableList<Int>? = null

    fun getRoadColors(): MutableList<Int> {
        colors?.let { return it }
        return ArrayList()
    }
}