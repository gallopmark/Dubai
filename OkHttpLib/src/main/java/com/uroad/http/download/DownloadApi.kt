package com.uroad.http.download

import io.reactivex.Observable
import okhttp3.ResponseBody
import org.jetbrains.annotations.NotNull
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author MFB
 * @create 2019/2/28
 * @describe  大文件官方建议用 @Streaming 来进行注解，不然会出现IO异常，小文件可以忽略不注入
 */
interface DownloadApi {
    /**
     * @param fileUrl 地址
     * @return ResponseBody
     */
    @Streaming
    @GET
    fun downloadFile(@NotNull @Url fileUrl: String): Observable<ResponseBody>
}
