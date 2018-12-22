package com.uroad.library.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class PhoneUtils {
    companion object {
        /**
         * 调用拨号界面
         *
         * @param phone 电话号码
         */
        fun call(context: Context, phone: String) {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}