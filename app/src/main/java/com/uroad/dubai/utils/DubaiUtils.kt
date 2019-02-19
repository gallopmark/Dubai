package com.uroad.dubai.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
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

        fun openGoogleServices(context: Context) {
            try {
                val url = "https://play.google.com/store/apps/details?id=${GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE}"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        }

        fun openNotificationSettings(context: Context) {
            try {
                val intent = Intent()
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> { //android 8.0引导
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> { //android 5.0-7.0
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", context.packageName)
                        intent.putExtra("app_uid", context.applicationInfo.uid)
                    }
                    else -> {
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", context.packageName, null)
                    }
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun openSettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.applicationContext.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        }

        fun isAppAlive(context: Context, packageName: String): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processList = activityManager.runningAppProcesses
            for (i in 0 until processList.size) {
                if (processList[i].processName == packageName) {
                    return true
                }
            }
            return false
        }
    }
}