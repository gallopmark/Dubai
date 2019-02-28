package com.uroad.http

import com.uroad.http.download.FileDownloadListener
import com.uroad.http.download.FileDownloader
import com.uroad.http.interceptor.HeaderInterceptor
import com.uroad.http.upload.FileUploadListener
import com.uroad.http.upload.SingleFileUploadListener
import com.uroad.http.upload.UploadManager
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitManager {

    /*you can initialize in the application*/
    fun init(baseUrl: String) {
        init(baseUrl, null)
    }

    fun init(baseUrl: String, options: HttpOptions?) {
        options?.let { config ->
            HttpClient.builder().writeTimeout(config.writeTimeout(), TimeUnit.MILLISECONDS)
                    .connectTimeout(config.connectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.readTimeout(), TimeUnit.MILLISECONDS)
                    .callTimeout(config.callTimeout(), TimeUnit.MILLISECONDS)
                    .cookieJar(config.cookieJar())
                    .cache(config.cache())
            config.headers()?.let { HttpClient.builder().addInterceptor(HeaderInterceptor(it)) }
        }
        RetrofitClient.builder().baseUrl(baseUrl).client(HttpClient.builder().build())
    }

    fun client(client: OkHttpClient): RetrofitManager {
        RetrofitClient.builder().client(client)
        return this
    }

    fun <K> createApi(cls: Class<K>): K = RetrofitClient.builder().build().create(cls)

    fun uploadFile(url: String, fileKey: String?, file: File): Disposable {
        return uploadFile(url, fileKey, file, null)
    }

    fun uploadFile(url: String, fileKey: String?, file: File, listener: SingleFileUploadListener?): Disposable {
        return UploadManager.uploadFile(url, fileKey, file, listener)
    }

    fun uploadFiles(url: String, fileKey: String?, files: MutableList<File>): Disposable {
        return uploadFiles(url, fileKey, files, null)
    }

    fun uploadFiles(url: String, fileKey: String?, files: MutableList<File>, listener: FileUploadListener?): Disposable {
        return UploadManager.uploadFileWithRequestBody(url, fileKey, files, listener)
    }

    fun download(url: String, targetPath: String): Disposable {
        return download(url, targetPath, null)
    }

    fun download(url: String, targetPath: String, listener: FileDownloadListener?): Disposable {
        return FileDownloader.download(url, targetPath, listener)
    }
}