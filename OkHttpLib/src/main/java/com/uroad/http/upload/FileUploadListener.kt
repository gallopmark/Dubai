package com.uroad.http.upload

import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

interface FileUploadListener {
    fun onStart(disposable: Disposable) {}
    fun onSuccess(responseBody: ResponseBody)
    fun onFailure(e: Throwable)
}