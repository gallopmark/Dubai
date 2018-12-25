package com.uroad.dubai.utils

import java.math.BigDecimal

class Utils {
    companion object {
        fun convertDistance(meter: Int): String {
            if (meter < 1000) return "${meter}m"
            val bigDecimal = BigDecimal(meter / 1000)
            bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP)
            return "${bigDecimal.toDouble()}km"
        }
    }
}