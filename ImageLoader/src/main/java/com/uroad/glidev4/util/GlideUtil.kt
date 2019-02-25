package com.uroad.glidev4.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.Size
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.uroad.glidev4.listener.*
import com.uroad.glidev4.tranform.BlurBitmapTransformation
import com.uroad.glidev4.tranform.RoundBitmapTransformation

class GlideUtil private constructor() {

    companion object {
        fun withRequestManager(`object`: Any): RequestManager? {
            var manager: RequestManager? = null
            when (`object`) {
                is View -> manager = Glide.with(`object`)
                is Context -> manager = Glide.with(`object`)
                is Activity -> manager = Glide.with(`object`)
                is Fragment -> manager = Glide.with(`object`)
                is FragmentActivity -> manager = Glide.with(`object`)
            }
            return manager
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView) {
            displayImage(`object`, url, imageView, -1)
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int) {
            val manager = GlideUtil.withRequestManager(`object`) ?: return
            manager.load(url).apply(buildOptions(defRes)).into(imageView)
        }

        private fun buildOptions(defRes: Int): RequestOptions {
            val option = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            if (defRes != -1) option.placeholder(defRes).error(defRes)
            return option
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
            val manager = GlideUtil.withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .transform(transformations)).into(imageView)
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, size: ImageSize) {
            displayImage(`object`, url, imageView, -1, size)
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, size: ImageSize) {
            val manager = GlideUtil.withRequestManager(`object`) ?: return
            manager.load(url).apply(buildOptions(defRes)
                    .override(size.width, size.height))
                    .into(imageView)
        }


        fun displayImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean) {
            val manager = GlideUtil.withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .skipMemoryCache(cacheInMemory))
                    .into(imageView)
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, listener: IImageLoaderListener) {
            displayImage(`object`, url, imageView, -1, listener)
        }

        fun displayImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener) {
            val manager = GlideUtil.withRequestManager(`object`) ?: return
            manager.load(url).apply(buildOptions(defRes))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            listener.onLoadingFailed(url, imageView, e)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            listener.onLoadingComplete(url, imageView)
                            return false
                        }
                    }).into(imageView)
        }

        fun displayCircleImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .transform(CircleCrop()))
                    .into(imageView)
        }

        fun displayRoundImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, radius: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .transform(RoundBitmapTransformation(radius)))
                    .into(imageView)
        }

        @SuppressLint("CheckResult")
        fun displayBlurImage(context: Context, url: String?, blurRadius: Int, listener: IGetDrawableListener) {
            Glide.with(context).load(url)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .transform(BlurBitmapTransformation(blurRadius)))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            listener.onDrawable(resource)
                            return false
                        }
                    })
        }

        fun displayBlurImage(`object`: Any, resId: Int, imageView: ImageView, blurRadius: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(resId)
                    .error(resId)
                    .transform(BlurBitmapTransformation(blurRadius)))
                    .into(imageView)
        }

        fun displayBlurImage(`object`: Any, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .transform(BlurBitmapTransformation(blurRadius)))
                    .into(imageView)
        }

        fun displayImageInResource(`object`: Any, resId: Int, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView)
        }

        fun displayImageInResource(`object`: Any, resId: Int, imageView: ImageView, transformations: BitmapTransformation) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(transformations))
                    .into(imageView)
        }

        fun displayImageInResource(`object`: Any, resId: Int, imageView: ImageView, defRes: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(defRes).error(defRes))
                    .into(imageView)
        }

        fun displayImageInResource(`object`: Any, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(defRes).error(defRes)
                            .transform(transformations))
                    .into(imageView)
        }

        fun displayImageInResourceTransform(`object`: Any, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(resId).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(errorResId)
                    .error(errorResId)
                    .transform(transformation))
                    .into(imageView)
        }

        fun displayImageByNet(`object`: Any, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(defRes)
                    .error(defRes)
                    .transform(transformation))
                    .into(imageView)
        }

        fun displayImageByDiskCacheStrategy(`object`: Any, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions().diskCacheStrategy(diskCacheStrategy)).into(imageView)
        }

        fun disPlayImageOnlyRetrieveFromCache(`object`: Any, url: String?, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions().onlyRetrieveFromCache(true)).into(imageView)
        }

        fun disPlayImageSkipMemoryCache(`object`: Any, url: String?, imageView: ImageView, skipFlag: Boolean, diskCacheStrategy: Boolean) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions()
                    .diskCacheStrategy(if (diskCacheStrategy) DiskCacheStrategy.NONE else DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(skipFlag))
                    .into(imageView)
        }

        fun disPlayImageErrorReload(`object`: Any, url: String?, fallbackUrl: String, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).error(manager.load(fallbackUrl)).into(imageView)
        }

        fun disPlayImageDisallowHardwareConfig(`object`: Any, url: String?, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            manager.load(url).apply(RequestOptions().disallowHardwareConfig()).into(imageView)
        }

        fun displayImageByTransition(`object`: Any, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            if (transitionOptions is DrawableTransitionOptions) {
                manager.load(url)
                        .transition(transitionOptions)
                        .into(imageView)
            } else {
                manager.asBitmap()
                        .load(url)
                        .transition(transitionOptions as BitmapTransitionOptions)
                        .into(imageView)
            }
        }

        fun displayImageThumbnail(`object`: Any, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            if (thumbnailSize == 0) {
                manager.load(url).thumbnail(manager.load(backUrl)).into(imageView)
            } else {
                //越小，图片越小，低网络的情况，图片越小
                // API 来强制 Glide 在缩略图请求中加载一个低分辨率图像
                manager.load(url).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .thumbnail(manager.load(backUrl).apply(RequestOptions().override(thumbnailSize)))
                        .into(imageView)
            }
        }

        fun displayImageThumbnail(`object`: Any, url: String?, thumbnailSize: Float, imageView: ImageView) {
            val manager = withRequestManager(`object`) ?: return
            if (thumbnailSize !in 0.0f..1.0f) return
            manager.load(url).thumbnail(thumbnailSize).into(imageView)
        }
    }
}