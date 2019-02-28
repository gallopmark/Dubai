package com.uroad.http.upload

import android.os.Handler
import android.os.Looper

import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import okio.Sink

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload progress of large
 * multipart requests.
 */
class RequestBodyWrapper constructor(private val delegate: RequestBody,
                                     private val mListener: SingleFileUploadListener?) : RequestBody() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var bufferedSink: BufferedSink? = null

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        try {
            return delegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val bufferedSink = this.bufferedSink ?: Okio.buffer(sink(sink)) //包装
        //写入
        delegate.writeTo(bufferedSink)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink?.flush()
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L
            //总字节长度，避免多次调用contentLength()方法
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                //回调上传接口
                handler.post { mListener?.onProgress(bytesWritten, contentLength, (bytesWritten * 100 / contentLength).toInt()) }
            }
        }
    }
}
