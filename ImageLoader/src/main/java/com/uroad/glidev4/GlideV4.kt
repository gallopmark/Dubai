package com.uroad.glidev4

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.uroad.glidev4.listener.IGetBitmapListener
import com.uroad.glidev4.listener.IGetDrawableListener
import com.uroad.glidev4.listener.IImageLoaderListener
import com.uroad.glidev4.listener.ImageSize
import com.uroad.glidev4.listener.OnGlideImageViewListener
import com.uroad.glidev4.listener.OnProgressListener
import java.io.File

object GlideV4 : IImageLoaderClient {
    private var client: IImageLoaderClient = GlideImageLoaderClient()

    override fun init(context: Context) {
        client.init(context)
    }

    override fun destroy(context: Context) {
        client.destroy(context)
    }

    override fun getCacheDir(context: Context): File? {
        return client.getCacheDir(context)
    }

    override fun clearMemoryCache(context: Context) {
        client.clearMemoryCache(context)
    }

    override fun clearDiskCache(context: Context) {
        client.clearDiskCache(context)
    }

    override fun getBitmapFromCache(context: Context, url: String?): Bitmap? {
        return client.getBitmapFromCache(context, url)
    }

    override fun getBitmapFromCache(context: Context, url: String?, listener: IGetBitmapListener) {
        client.getBitmapFromCache(context, url, listener)
    }

    override fun displayImage(context: Context, resId: Int, imageView: ImageView) {
        client.displayImage(context, resId, imageView)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView) {
        client.displayImage(context, url, imageView)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, isCache: Boolean) {
        client.displayImage(context, url, imageView, isCache)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView) {
        client.displayImage(fragment, url, imageView)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int) {
        client.displayImage(context, url, imageView, defRes)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int) {
        client.displayImage(fragment, url, imageView, defRes)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        client.displayImage(context, url, imageView, defRes, transformations)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        client.displayImage(fragment, url, imageView, defRes, transformations)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, size: ImageSize) {
        client.displayImage(context, url, imageView, defRes, size)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, size: ImageSize) {
        client.displayImage(fragment, url, imageView, defRes, size)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, size: ImageSize) {
        client.displayImage(context, url, imageView, size)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, size: ImageSize) {
        client.displayImage(fragment, url, imageView, size)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean) {
        client.displayImage(context, url, imageView, defRes, cacheInMemory)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean) {
        client.displayImage(fragment, url, imageView, defRes, cacheInMemory)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, listener: IImageLoaderListener) {
        client.displayImage(context, url, imageView, listener)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, listener: IImageLoaderListener) {
        client.displayImage(fragment, url, imageView, listener)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener) {
        client.displayImage(context, url, imageView, defRes, listener)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener) {
        client.displayImage(fragment, url, imageView, defRes, listener)
    }

    override fun displayCircleImage(context: Context, url: String?, imageView: ImageView, defRes: Int) {
        client.displayImage(context, url, imageView, defRes)
    }

    override fun displayCircleImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int) {
        client.displayImage(fragment, url, imageView, defRes)
    }

    override fun displayRoundImage(context: Context, url: String?, imageView: ImageView, defRes: Int, radius: Int) {
        client.displayRoundImage(context, url, imageView, defRes, radius)
    }

    override fun displayRoundImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, radius: Int) {
        client.displayRoundImage(fragment, url, imageView, defRes, radius)
    }

    override fun displayBlurImage(context: Context, url: String?, blurRadius: Int, listener: IGetDrawableListener) {
        client.displayBlurImage(context, url, blurRadius, listener)
    }

    override fun displayBlurImage(context: Context, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int) {
        client.displayBlurImage(context, url, imageView, defRes, blurRadius)
    }

    override fun displayBlurImage(context: Context, resId: Int, imageView: ImageView, blurRadius: Int) {
        client.displayBlurImage(context, resId, imageView, blurRadius)
    }

    override fun displayBlurImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int) {
        client.displayBlurImage(fragment, url, imageView, defRes, blurRadius)
    }

    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView) {
        client.displayImageInResource(context, resId, imageView)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView) {
        client.displayImageInResource(fragment, resId, imageView)
    }

    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, transformations: BitmapTransformation) {
        client.displayImageInResource(context, resId, imageView, transformations)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, transformations: BitmapTransformation) {
        client.displayImageInResource(fragment, resId, imageView, transformations)
    }

    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int) {
        client.displayImageInResource(context, resId, imageView, defRes)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int) {
        client.displayImageInResource(fragment, resId, imageView, defRes)
    }

    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        client.displayImageInResource(context, resId, imageView, defRes, transformations)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        client.displayImageInResource(fragment, resId, imageView, defRes, transformations)
    }

    override fun displayImageInResourceTransform(activity: Activity, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        client.displayImageInResourceTransform(activity, resId, imageView, transformation, errorResId)
    }

    override fun displayImageInResourceTransform(context: Context, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        client.displayImageInResourceTransform(context, resId, imageView, transformation, errorResId)
    }

    override fun displayImageInResourceTransform(fragment: Fragment, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        client.displayImageInResourceTransform(fragment, resId, imageView, transformation, errorResId)
    }

    override fun displayImageByNet(context: Context, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        client.displayImageByNet(context, url, imageView, defRes, transformation)
    }

    override fun displayImageByNet(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        client.displayImageByNet(fragment, url, imageView, defRes, transformation)
    }

    override fun displayImageByNet(activity: Activity, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        client.displayImageByNet(activity, url, imageView, defRes, transformation)
    }

    override fun clear(activity: Activity, imageView: ImageView) {
        client.clear(activity, imageView)
    }

    override fun clear(context: Context, imageView: ImageView) {
        client.clear(context, imageView)
    }

    override fun clear(fragment: Fragment, imageView: ImageView) {
        client.clear(fragment, imageView)
    }

    override fun displayImageByDiskCacheStrategy(fragment: Fragment, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        client.displayImageByDiskCacheStrategy(fragment, url, diskCacheStrategy, imageView)
    }

    override fun displayImageByDiskCacheStrategy(activity: Activity, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        client.displayImageByDiskCacheStrategy(activity, url, diskCacheStrategy, imageView)
    }

    override fun displayImageByDiskCacheStrategy(context: Context, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        client.displayImageByDiskCacheStrategy(context, url, diskCacheStrategy, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(fragment: Fragment, url: String?, imageView: ImageView) {
        client.disPlayImageOnlyRetrieveFromCache(fragment, url, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(activity: Activity, url: String?, imageView: ImageView) {
        client.disPlayImageOnlyRetrieveFromCache(activity, url, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(context: Context, url: String?, imageView: ImageView) {
        client.disPlayImageOnlyRetrieveFromCache(context, url, imageView)
    }

    override fun disPlayImageSkipMemoryCache(fragment: Fragment, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        client.disPlayImageSkipMemoryCache(fragment, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageSkipMemoryCache(activity: Activity, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        client.disPlayImageSkipMemoryCache(activity, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageSkipMemoryCache(context: Context, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        client.disPlayImageSkipMemoryCache(context, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageErrorReload(fragment: Fragment, url: String?, fallbackUrl: String, imageView: ImageView) {
        client.disPlayImageErrorReload(fragment, url, fallbackUrl, imageView)
    }

    override fun disPlayImageErrorReload(activity: Activity, url: String?, fallbackUrl: String, imageView: ImageView) {
        client.disPlayImageErrorReload(activity, url, fallbackUrl, imageView)
    }

    override fun disPlayImageErrorReload(context: Context, url: String?, fallbackUrl: String, imageView: ImageView) {
        client.disPlayImageErrorReload(context, url, fallbackUrl, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(fragment: Fragment, url: String?, imageView: ImageView) {
        client.disPlayImagedisallowHardwareConfig(fragment, url, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(activity: Activity, url: String?, imageView: ImageView) {
        client.disPlayImagedisallowHardwareConfig(activity, url, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(context: Context, url: String?, imageView: ImageView) {
        client.disPlayImagedisallowHardwareConfig(context, url, imageView)
    }

    override fun disPlayImageProgress(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        client.disPlayImageProgress(context, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgress(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        client.disPlayImageProgress(activity, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgress(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        client.disPlayImageProgress(fragment, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgressByOnProgressListener(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        client.disPlayImageProgressByOnProgressListener(context, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    override fun disPlayImageProgressByOnProgressListener(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        client.disPlayImageProgressByOnProgressListener(activity, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    override fun disPlayImageProgressByOnProgressListener(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        client.disPlayImageProgressByOnProgressListener(fragment, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    override fun displayImageByTransition(context: Context, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        client.displayImageByTransition(context, url, transitionOptions, imageView)
    }

    override fun displayImageByTransition(activity: Activity, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        client.displayImageByTransition(activity, url, transitionOptions, imageView)
    }

    override fun displayImageByTransition(fragment: Fragment, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        client.displayImageByTransition(fragment, url, transitionOptions, imageView)
    }

    override fun glidePauseRequests(context: Context) {
        client.glidePauseRequests(context)
    }

    override fun glidePauseRequests(activity: Activity) {
        client.glidePauseRequests(activity)
    }

    override fun glidePauseRequests(fragment: Fragment) {
        client.glidePauseRequests(fragment)
    }

    override fun glideResumeRequests(context: Context) {
        client.glideResumeRequests(context)
    }

    override fun glideResumeRequests(activity: Activity) {
        client.glideResumeRequests(activity)
    }

    override fun glideResumeRequests(fragment: Fragment) {
        client.glideResumeRequests(fragment)
    }

    override fun displayImageThumbnail(context: Context, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        client.displayImageThumbnail(context, url, backUrl, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(activity: Activity, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        client.displayImageThumbnail(activity, url, backUrl, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(fragment: Fragment, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        client.displayImageThumbnail(fragment, url, backUrl, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(fragment: Fragment, url: String?, thumbnailSize: Float, imageView: ImageView) {
        client.displayImageThumbnail(fragment, url, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(activity: Activity, url: String?, thumbnailSize: Float, imageView: ImageView) {
        client.displayImageThumbnail(activity, url, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(context: Context, url: String?, thumbnailSize: Float, imageView: ImageView) {
        client.displayImageThumbnail(context, url, thumbnailSize, imageView)
    }

}