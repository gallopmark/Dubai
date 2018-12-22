package com.uroad.library.utils

import java.security.MessageDigest
import kotlin.experimental.and

class MD5Utils {
    companion object {
        /**
         * 将字符串转换为MD5加密（32位小写）
         */
        fun encoderByMd5(str: String): String {
            try {
                val md5 = MessageDigest.getInstance("md5")// 返回实现指定摘要算法的
                md5.update(str.toByteArray())// 先将字符串转换成byte数组，再用byte 数组更新摘要
                val nStr = md5.digest()// 哈希计算，即加密
                return bytes2Hex(nStr)// 加密的结果是byte数组，将byte数组转换成字符串
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * 将byte数组转换为字符串
         */
        private fun bytes2Hex(bts: ByteArray): String {
            val des = StringBuilder()
            for (bt in bts) {
                val tmp = Integer.toHexString(bt.toInt() and 0xFF)
                if (tmp.length == 1) {
                    des.append("0")
                }
                des.append(tmp)
            }
            return des.toString()
        }
    }
}