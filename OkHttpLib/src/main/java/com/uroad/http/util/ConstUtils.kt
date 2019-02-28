package com.uroad.http.util

import com.uroad.http.RetrofitManager
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ConstUtils {
    companion object {
        fun buildManager(): RetrofitManager {
            return RetrofitManager.client(OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build())
        }
    }
}