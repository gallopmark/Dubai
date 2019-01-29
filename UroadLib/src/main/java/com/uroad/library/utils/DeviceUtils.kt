package com.uroad.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

import java.io.File
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections
import kotlin.experimental.and

@Suppress("DEPRECATION")
class DeviceUtils {
    companion object {
        /**
         * 判断设备是否root
         *
         * @return the boolean`true`: 是<br></br>`false`: 否
         */
        fun isDeviceRoot(): Boolean {
            val su = "su"
            val locations = arrayOf("/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/")
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }

        /**
         * 获取设备系统版本号
         *
         * @return 设备系统版本号
         */
        fun getSDKVersion(): Int {
            return Build.VERSION.SDK_INT
        }

        /**
         * 获取设备AndroidID
         *
         * @param context 上下文
         * @return AndroidID
         */
        @SuppressLint("HardwareIds")
        fun getAndroidID(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        /**
         * 获取设备MAC地址
         *
         * 需添加权限 `<uses-permission android:name="android.permission.INTERNET"/>`
         *
         * @return MAC地址
         */
        private fun getMacAddressByNetworkInterface(): String {
            try {
                val nis = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (ni in nis) {
                    if (!ni.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.size > 0) {
                        val res1 = StringBuilder()
                        for (b in macBytes) {
                            res1.append(String.format("%02x:", b))
                        }
                        return res1.deleteCharAt(res1.length - 1).toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }

        /**
         * 获取设备厂商
         *
         * 如Xiaomi
         *
         * @return 设备厂商
         */

        fun getManufacturer(): String {
            return Build.MANUFACTURER
        }

        /*设备的唯一标识。由设备的多个信息拼接合成。*/
        fun getFingerprint(): String {
            return Build.FINGERPRINT
        }

        /**
         * 获取当前手机系统版本号
         *
         * @return  系统版本号
         */
        fun getSystemVersion(): String {
            return android.os.Build.VERSION.RELEASE ?: ""
        }

        /**
         * 获取设备型号
         *
         * 如MI2SC
         *
         * @return 设备型号
         */
        fun getModel(): String {
            var model: String? = Build.MODEL
            model = model?.trim()?.replace("\\s*".toRegex(), "") ?: ""
            return model
        }

        /**
         * 关机
         *
         * 需系统权限 `<android:sharedUserId="android.uid.system"/>`
         *
         * @param context 上下文
         */
        fun shutdown(context: Context) {
            val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        /**
         * 重启
         *
         * 需系统权限 `<android:sharedUserId="android.uid.system"/>`
         *
         * @param context 上下文
         */
        fun reboot(context: Context) {
            val intent = Intent(Intent.ACTION_REBOOT)
            intent.putExtra("nowait", 1)
            intent.putExtra("interval", 1)
            intent.putExtra("window", 0)
            context.sendBroadcast(intent)
        }

        /**
         * 重启
         *
         * 需系统权限 `<android:sharedUserId="android.uid.system"/>`
         *
         * @param context 上下文
         * @param reason  传递给内核来请求特殊的引导模式，如"recovery"
         */
        fun reboot(context: Context, reason: String) {
            val mPowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            try {
                mPowerManager.reboot(reason)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        @SuppressLint("HardwareIds", "MissingPermission")
        fun getUniqueId(context: Context): String {
            val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val id = androidID + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
            return try {
                toMD5(id)
            } catch (e: Exception) {
                e.printStackTrace()
                id
            }
        }


        @Throws(Exception::class)
        private fun toMD5(text: String): String {
            //获取摘要器 MessageDigest
            val messageDigest = MessageDigest.getInstance("MD5")
            //通过摘要器对字符串的二进制字节数组进行hash计算
            val digest = messageDigest.digest(text.toByteArray())
            val sb = StringBuilder()
            for (aDigest in digest) {
                //循环每个字符 将计算结果转化为正整数;
                val digestInt = aDigest.toInt() and 0xff
                //将10进制转化为较短的16进制
                val hexString = Integer.toHexString(digestInt)
                //转化结果如果是个位数会省略0,因此判断并补0
                if (hexString.length < 2) {
                    sb.append(0)
                }
                //将循环结果添加到缓冲区
                sb.append(hexString)
            }
            //返回整个结果
            return sb.toString()
        }
    }

}
