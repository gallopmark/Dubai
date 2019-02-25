package com.uroad.glidev4.listener

import android.widget.ImageView

/**
 * 监听图片下载进度
 */
interface IImageLoaderListener {

    //监听图片下载错误
    fun onLoadingFailed(url: String?, target: ImageView, exception: Exception?)

    //监听图片加载成功
    fun onLoadingComplete(url: String?, target: ImageView)

}
