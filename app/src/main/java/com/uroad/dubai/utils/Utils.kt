package com.uroad.dubai.utils

import android.content.Context
import android.location.Geocoder
import java.math.BigDecimal
import java.util.*

class Utils {
    companion object {
        fun convertDistance(meter: Int): String {
            if (meter < 1000) return "${meter}m"
            val bigDecimal = BigDecimal(meter / 1000)
            bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
            return "${bigDecimal.toDouble()}km"
        }

        fun convertAddress(context: Context, latitude: Double, longitude: Double): String {
            val mStringBuilder = StringBuilder()
            try {
                val mAddresses = Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 3)
                if (!mAddresses.isEmpty()) {
                    val address = mAddresses[0]
                    mStringBuilder.append(address.locality)
                            .append(address.subLocality)
                            .append(address.thoroughfare)
                            .append(address.subThoroughfare)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mStringBuilder.toString()
        }
    }
}