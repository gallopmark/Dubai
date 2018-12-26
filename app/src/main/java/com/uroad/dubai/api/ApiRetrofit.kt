package com.uroad.dubai.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiRetrofit {
    private const val BASE_URL = "http://dubaiatis.u-road.com/DubaiApi/index.php/api/" //ApiIndex
    private val client = OkHttpClient.Builder()
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //rxjava2转换
            .addConverterFactory(ScalarsConverterFactory.create()) //string返回
            .client(client)
            .build()

    fun <K> createApi(cls: Class<K>): K = retrofit.create(cls)
}