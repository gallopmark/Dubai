package com.uroad.dubai.local

import android.content.Context

class AppSource : DataSource() {
    companion object {
        private const val GUIDE_KEY = "isGuide"  //是否已经展现引导图
        private const val WELCOME_KEY = "isWelcome" //是否已经展示欢迎页面
        private const val SEARCH_HISTORY_KEY = "isShowHistory"  //是否打开搜索历史
        private const val LOAD_PHOTO_KEY = "isLoadPhoto"  //app是否加载图片开关

        private fun getSharedPreferences(context: Context) = context.getSharedPreferences(APP_FILE, Context.MODE_PRIVATE)

        fun saveGuide(context: Context) = getSharedPreferences(context).edit().putBoolean(GUIDE_KEY, true).apply()
        fun isGuide(context: Context) = getSharedPreferences(context).getBoolean(GUIDE_KEY, false)

        fun saveWelcome(context: Context) = getSharedPreferences(context).edit().putBoolean(WELCOME_KEY, true).apply()
        fun isWelcome(context: Context) = getSharedPreferences(context).getBoolean(WELCOME_KEY, false)

        fun isShowSearchHistory(context: Context) = getSharedPreferences(context).getBoolean(SEARCH_HISTORY_KEY, true)

        fun setShowSearchHistory(context: Context, isShow: Boolean) = getSharedPreferences(context).edit().putBoolean(SEARCH_HISTORY_KEY, isShow).apply()

        fun isCanLoadPhoto(context: Context) = getSharedPreferences(context).getBoolean(LOAD_PHOTO_KEY, true)

        fun setCanLoadPhoto(context: Context, isLoadPhoto: Boolean) = getSharedPreferences(context).edit().putBoolean(LOAD_PHOTO_KEY, isLoadPhoto).apply()
    }
}