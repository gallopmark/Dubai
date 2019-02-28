package com.uroad.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor constructor(private val headers: HashMap<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        for ((k, v) in headers) {
            request.addHeader(k, v)
        }
        return chain.proceed(request.build())
    }
}