package com.uroad.http.upload

interface SingleFileUploadListener : FileUploadListener {
    fun onProgress(bytesWritten: Long, contentLength: Long, progress: Int){}
}