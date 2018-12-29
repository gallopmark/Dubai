package com.uroad.dubai.model

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

class RouteMDL {

    var newsid: String? = null
    var publishtime: String? = null
    var title: String? = null
    var content: String? = null
    var headimg: String? = null
    var subtitle: String? = null
    var longitude: Double? = 0.0
    var latitude: Double? = 0.0
    var address: String? = null
    var phone: String? = null
    var website: String? = null
    var hours: String? = null
    var commentstar: String? = null
    var distance: String? = null
    var colors: MutableList<Int>? = null

    fun getRoadColors(): MutableList<Int> {
        colors?.let { return it }
        return ArrayList()
    }

    fun getTime(): String {
        if (TextUtils.isEmpty(publishtime)) return ""
        try {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val time = format.parse(publishtime).time
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
        } catch (e: Exception) {
            return ""
        }
    }

}