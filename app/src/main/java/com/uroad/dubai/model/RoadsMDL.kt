package com.uroad.dubai.model

class RoadsMDL {
    var icon: Int = 0
    var title: String? = null
    var content: String? = null
    var distance: String? = null
    var colors: MutableList<Int>? = null

    fun getRoadColors(): MutableList<Int> {
        colors?.let { return it }
        return ArrayList()
    }
}