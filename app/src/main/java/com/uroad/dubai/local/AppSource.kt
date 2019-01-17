package com.uroad.dubai.local

import android.content.Context

class AppSource : DataSource() {
    companion object {
        private const val GUIDE_KEY = "isGuide"  //是否已经展现引导图
        private const val WELCOME_KEY = "isWelcome" //是否已经展示欢迎页面

        private fun getSharedPreferences(context: Context) = context.getSharedPreferences(APP_FILE, Context.MODE_PRIVATE)

        fun saveGuide(context: Context) = getSharedPreferences(context).edit().putBoolean(GUIDE_KEY, true).apply()
        fun isGuide(context: Context) = getSharedPreferences(context).getBoolean(GUIDE_KEY, false)

        fun saveWelcome(context: Context) = getSharedPreferences(context).edit().putBoolean(WELCOME_KEY, true).apply()
        fun isWelcome(context: Context) = getSharedPreferences(context).getBoolean(WELCOME_KEY, false)
    }
}