package com.uroad.library.utils

import android.text.TextUtils

class VersionUtils {
    companion object {
        fun isNeedUpdate(versionServer: String, versionLocal: String): Boolean {
            if (TextUtils.isEmpty(versionServer) || TextUtils.isEmpty(versionLocal))
                return false
            var index1 = 0
            var index2 = 0
            while (index1 < versionServer.length && index2 < versionLocal.length) {
                val number1 = getValue(versionServer, index1)
                val number2 = getValue(versionLocal, index2)
                when {
                    number1[0] < number2[0] -> return false
                    number1[0] > number2[0] -> return true
                    else -> {
                        index1 = number1[1] + 1
                        index2 = number2[1] + 1
                    }
                }
            }
            return (index1 != versionServer.length || index2 != versionLocal.length) && index1 < versionServer.length
        }

        /**
         * @param index the starting point
         * @return the number between two dots, and the index of the dot
         */
        private fun getValue(version: String, index: Int): IntArray {
            var newIndex = index
            val valueIndex = IntArray(2)
            val sb = StringBuilder()
            while (newIndex < version.length && version[newIndex] != '.') {
                sb.append(version[newIndex])
                newIndex++
            }
            valueIndex[0] = Integer.parseInt(sb.toString())
            valueIndex[1] = newIndex
            return valueIndex
        }
    }
}