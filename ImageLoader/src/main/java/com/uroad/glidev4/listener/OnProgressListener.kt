package com.uroad.glidev4.listener

import com.bumptech.glide.load.engine.GlideException


interface OnProgressListener {
    /**
     *
     * @param imageUrl 图片地址
     * @param bytesRead 下载了多少字节
     * @param totalBytes 总共的大小
     * @param isDone 是否完成
     * @param exception 异常
     */
    fun onProgress(imageUrl: String?, bytesRead: Long, totalBytes: Long, isDone: Boolean, exception: GlideException?)
}
