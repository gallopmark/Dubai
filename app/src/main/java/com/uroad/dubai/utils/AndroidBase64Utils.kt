package com.uroad.dubai.utils

import android.util.Base64

/**
 *Created by MFB on 2018/7/27.
 * android base64加密和解密
 */
class AndroidBase64Utils private constructor() {
    companion object {
        fun encodeToString(content: String?): String {
            return try {
                if (content != null) Base64.encodeToString(content.toByteArray(), Base64.DEFAULT)
                else ""
            } catch (e: Exception) {
                ""
            }
        }

        fun decodeToString(content: String?): String {
            return try {
                if (content != null) String(Base64.decode(content.toByteArray(), Base64.DEFAULT))
                else ""
            } catch (e: Exception) {
                ""
            }
        }
    }
}