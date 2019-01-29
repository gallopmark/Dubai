package com.uroad.dubai.utils

import android.content.Context
import android.location.LocationManager
import android.os.Build
import java.math.BigDecimal

class DubaiUtils {
    companion object {
        fun convertDistance(meter: Int): String {
            if (meter < 1000) return "${meter}m"
            val bigDecimal = BigDecimal(meter / 1000)
            bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
            return "${bigDecimal.toDouble()}km"
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return locationManager.isLocationEnabled
            }
            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (gps || network) {
                return true
            }
            return false
        }
    }
}