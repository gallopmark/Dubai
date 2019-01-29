package com.uroad.dubai.api.upload

import io.reactivex.disposables.Disposable

/**
 *Created by MFB on 2018/8/2.
 */
abstract class UploadFileCallback {
    open fun onStart(disposable: Disposable) {}

    open fun onProgress(bytesWritten: Long, contentLength: Long, progress: Int) {}

    //上传成功的回调
    abstract fun onSuccess(json: String)

    open fun onComplete() {}

    //上传失败回调
    abstract fun onFailure(e: Throwable)
}