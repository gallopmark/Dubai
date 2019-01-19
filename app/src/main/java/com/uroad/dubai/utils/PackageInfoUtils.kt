package com.uroad.dubai.utils

import android.content.Context
import android.support.v4.content.pm.PackageInfoCompat


/**
 *Created by MFB on 2018/8/28.
 */
class PackageInfoUtils {
    companion object {
        fun getVersionCode(context: Context): Long {
            return try {
                val packageInfo = context.applicationContext.packageManager.getPackageInfo(context.packageName, 0)
                return PackageInfoCompat.getLongVersionCode(packageInfo)
            } catch (e: Exception) {
                0
            }
        }

        fun getVersionName(context: Context): String {
            return try {
                val packageInfo = context.applicationContext.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName
            } catch (e: Exception) {
                "1.0"
            }
        }
    }
}