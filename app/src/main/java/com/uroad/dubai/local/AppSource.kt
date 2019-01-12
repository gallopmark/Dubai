package com.uroad.dubai.local

import android.content.Context

class AppSource {
    companion object {
        private const val APP_FILE = DataSource.APP_FILE
        private const val GUIDE_KEY = "isGuide"  //是否已经展现引导图

        private fun getSharedPreferences(context: Context) = context.getSharedPreferences(APP_FILE, Context.MODE_PRIVATE)

        fun saveGuide(context: Context) = getSharedPreferences(context).edit().putBoolean(GUIDE_KEY, true).apply()
        fun isGuide(context: Context) = getSharedPreferences(context).getBoolean(GUIDE_KEY, false)
    }
}