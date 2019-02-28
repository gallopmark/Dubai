package com.uroad.http

import okhttp3.OkHttpClient

object HttpClient {
    private val builder = OkHttpClient.Builder()
    fun builder() = builder
}