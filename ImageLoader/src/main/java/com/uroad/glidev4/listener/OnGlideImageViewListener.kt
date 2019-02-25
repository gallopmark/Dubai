package com.uroad.glidev4.listener

import com.bumptech.glide.load.engine.GlideException


interface OnGlideImageViewListener {
    /**
     *
     * @param percent 下载进度的百分比，不关心，大小
     * @param isDone 是否完成
     * @param exception 异常
     */
    fun onProgress(percent: Int, isDone: Boolean, exception: GlideException?)
}
