package com.uroad.dubai.api

import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.common.DubaiConfig
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.library.utils.DeviceUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

@Deprecated("Technical Support HttpLib")
object ApiRetrofit {
    private const val DEBUG_UTL = "http://dubaiatis.u-road.com/DubaiApi/index.php/api/"
    private const val API_URL = "http://dubaiatis.u-road.com/DubaiApi/index.php/api/"
    private val BASE_URL = if (DubaiConfig.isApiDebug) DEBUG_UTL else API_URL
    private val client = OkHttpClient.Builder()
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor {
                val request = it.request().newBuilder()
                        .addHeader("x-app-type", "Android")
                        .addHeader("x-user-uuid", DubaiApplication.getUserId())
                        .addHeader("x-app-version", PackageInfoUtils.getVersionName(DubaiApplication.instance))
                        .addHeader("x-device-uuid", DeviceUtils.getAndroidID(DubaiApplication.instance))
                        .addHeader("x-device-info", DeviceUtils.getModel())
                        .addHeader("x-device-systemversion", DeviceUtils.getSystemVersion())
                        .build()
                return@addInterceptor it.proceed(request)
            }
            .build()
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //rxjava2转换
            .addConverterFactory(ScalarsConverterFactory.create()) //string返回
            .client(client)
            .build()

    fun <K> createApi(cls: Class<K>): K = retrofit.create(cls)
}