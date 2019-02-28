package com.uroad.http.download

import io.reactivex.disposables.Disposable

/**
 * @author MFB
 * @create 2019/2/28
 * @describe File download listener
 */
interface FileDownloadListener {
    /*UIThread*/
    fun onStart(disposable: Disposable) {}

    fun onProgress(bytesRead: Long, contentLength: Long, progress: Float) {}

    fun onDownloadCompleted(filePath: String)

    fun onError(e: Throwable)
}