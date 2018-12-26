package com.uroad.dubai.webService

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.uroad.dubai.api.ApiRetrofit
import com.uroad.dubai.utils.AndroidBase64Utils
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    companion object {
        fun createRequestBody(method: String?, params: Map<String, String?>): RequestBody {
            val map = HashMap<String, String?>().apply { put("fun", method) }
            val param = Gson().toJson(params)
            val jsonObject1 = JsonParser().parse(param).asJsonObject
            val jsonObject2 = JsonParser().parse(Gson().toJson(map)).asJsonObject
            jsonObject2.add("data", jsonObject1)
            val content = AndroidBase64Utils.encodeToString(jsonObject2.toString())
            return RequestBody.create(MediaType.parse("application/json"), content)
        }

        fun doRequest(method: String?, params: Map<String, String?>): Observable<String> {
            val body = createRequestBody(method, params)
            return ApiRetrofit.createApi(ApiService::class.java).doPost(body)
        }
    }

    @POST("ApiIndex")
    fun doPost(@Body body: RequestBody): Observable<String>
}