package com.uroad.dubai.webService

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.uroad.dubai.common.DubaiConfig
import com.uroad.dubai.utils.AndroidBase64Utils
import com.uroad.http.RetrofitManager
import com.uroad.library.utils.DeviceUtils
import com.uroad.mqtt.MQTTService
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    companion object {
        private const val DEBUG_UTL = "http://dubaiatis.u-road.com/DubaiApi/index.php/api/"
        private const val API_URL = "http://dubaiatis.u-road.com/DubaiApi/index.php/api/"
        val BASE_URL = if (DubaiConfig.isApiDebug) DEBUG_UTL else API_URL
        val UPLOAD_URL = "${BASE_URL}Upload/uploadImg"
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
            return RetrofitManager.createApi(ApiService::class.java).doPost(body)
//            return ApiRetrofit.createApi(ApiService::class.java).doPost(body)
        }

        private const val MQ_SERVICE_URL = "tcp://115.238.84.148:11012"
        private const val MQ_USER = "zhejiang"
        private const val MQ_PASSWORD = "uroad123"
        const val TOPIC_CLOSE_TEAM = "ZhgsMq/CloseTeam/"  //解散车队
        const val TOPIC_ADD_TEAM = "ZhgsMq/AddTeam/"  //加入车队
        const val TOPIC_QUIT_TEAM = "ZhgsMq/QuitTeam/"  //退出车队
        const val TOPIC_SEND_MSG = "ZhgsMq/TeamSendMsg/"  //发送语音消息
        const val TOPIC_LOC_UPDATE = "ZhgsMq/TeamLocUpdate/" //成员位置发送变动后需要通知车队其他人员更新信息
        const val TOPIC_PLACE_UPDATE = "ZhgsMq/TeamPlaceUpdate/" //目的地更新通知车队各成员
        const val TOPIC_MSG_CALLBACK = "ZhgsMq/CallBack/" //接口响应通知操作人结果。   Java后台触发

        fun buildMQTTService(context: Context): MQTTService = MQTTService.Builder(context)
                .autoReconnect(true)
                .clientId(DeviceUtils.getAndroidID(context))
                .serverUrl(MQ_SERVICE_URL)
                .userName(MQ_USER)
                .passWord(MQ_PASSWORD)
                .timeOut(30)
                .keepAliveInterval(10)
                .build()
    }

    @POST("ApiIndex")
    fun doPost(@Body body: RequestBody): Observable<String>

    @Multipart
    @POST("Upload/uploadImg")
    fun uploadFile(@Part part: MultipartBody.Part): Observable<ResponseBody>
}