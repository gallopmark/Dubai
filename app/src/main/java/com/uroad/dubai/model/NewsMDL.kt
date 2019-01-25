package com.uroad.dubai.model

import android.text.TextUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import java.text.SimpleDateFormat
import java.util.*

open class NewsMDL {
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
    var newstype: String? = null

    fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }

    fun getTime(): String {
        if (TextUtils.isEmpty(publishtime)) return ""
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val time = format.parse(publishtime).time
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
        } catch (e: Exception) {
            ""
        }
    }
}