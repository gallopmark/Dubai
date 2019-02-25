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

/**
 * Created by shiming on 2016/10/26.
 */

interface IImageLoaderClient {
    fun init(context: Context)

    fun destroy(context: Context)

    fun getCacheDir(context: Context): File?

    fun clearMemoryCache(context: Context)

    fun clearDiskCache(context: Context)

    fun getBitmapFromCache(context: Context, url: String?): Bitmap?

    fun getBitmapFromCache(context: Context, url: String?, listener: IGetBitmapListener)

    fun displayImage(context: Context, resId: Int, imageView: ImageView)

    fun displayImage(context: Context, url: String?, imageView: ImageView)
    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView)

    fun displayImage(context: Context, url: String?, imageView: ImageView, isCache: Boolean)
    fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int)

    fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation)

    fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, size: ImageSize)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, size: ImageSize)

    fun displayImage(context: Context, url: String?, imageView: ImageView, size: ImageSize)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, size: ImageSize)

    fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean)

    fun displayImage(context: Context, url: String?, imageView: ImageView, listener: IImageLoaderListener)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, listener: IImageLoaderListener)

    fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener)

    fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener)


    fun displayCircleImage(context: Context, url: String?, imageView: ImageView, defRes: Int)

    fun displayCircleImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int)

    fun displayRoundImage(context: Context, url: String?, imageView: ImageView, defRes: Int, radius: Int)

    fun displayRoundImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, radius: Int)

    fun displayBlurImage(context: Context, url: String?, blurRadius: Int, listener: IGetDrawableListener)

    fun displayBlurImage(context: Context, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int)

    fun displayBlurImage(context: Context, resId: Int, imageView: ImageView, blurRadius: Int)

    fun displayBlurImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int)

    fun displayImageInResource(context: Context, resId: Int, imageView: ImageView)

    fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView)

    fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, transformations: BitmapTransformation)

    fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, transformations: BitmapTransformation)

    fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int)

    fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int)

    fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation)

    fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation)


    //add shiming   2018.4.20 transformation 需要装换的那种图像的风格，错误图片，或者是，正在加载中的错误图
    fun displayImageInResourceTransform(activity: Activity, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int)

    fun displayImageInResourceTransform(context: Context, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int)

    fun displayImageInResourceTransform(fragment: Fragment, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int)

    //这是对网络图片，进行的图片操作，使用的glide中的方法
    fun displayImageByNet(context: Context, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation)

    fun displayImageByNet(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation)

    fun displayImageByNet(activity: Activity, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation)


    /**
     * 停止图片的加载，对某一个的Activity
     */
    fun clear(activity: Activity, imageView: ImageView)

    /**
     * 停止图片的加载，context
     */
    fun clear(context: Context, imageView: ImageView)

    /**
     * 停止图片的加载，fragment
     */
    fun clear(fragment: Fragment, imageView: ImageView)


    //如果需要的话，需要指定加载中，或者是失败的图片
    fun displayImageByDiskCacheStrategy(fragment: Fragment, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView)

    fun displayImageByDiskCacheStrategy(activity: Activity, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView)

    fun displayImageByDiskCacheStrategy(context: Context, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView)

    //某些情形下，你可能希望只要图片不在缓存中则加载直接失败（比如省流量模式）
    fun disPlayImageOnlyRetrieveFromCache(fragment: Fragment, url: String?, imageView: ImageView)

    fun disPlayImageOnlyRetrieveFromCache(activity: Activity, url: String?, imageView: ImageView)

    fun disPlayImageOnlyRetrieveFromCache(context: Context, url: String?, imageView: ImageView)


    /**
     * 如果你想确保一个特定的请求跳过磁盘和/或内存缓存（比如，图片验证码 –）
     *
     * @param skipflag         是否跳过内存缓存
     * @param diskCacheStratey 是否跳过磁盘缓存
     */
    fun disPlayImageSkipMemoryCache(fragment: Fragment, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean)

    fun disPlayImageSkipMemoryCache(activity: Activity, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean)

    fun disPlayImageSkipMemoryCache(context: Context, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean)

    /**
     * 知道这个图片会加载失败，那么的话，我们可以重新加载
     */
    //从 Glide 4.3.0 开始，你可以很轻松地使用 .error() 方法。这个方法接受一个任意的 RequestBuilder,它会且只会在主请求失败时开始一个新的请求：
    fun disPlayImageErrorReload(fragment: Fragment, url: String?, fallbackUrl: String, imageView: ImageView)

    fun disPlayImageErrorReload(activity: Activity, url: String?, fallbackUrl: String, imageView: ImageView)

    fun disPlayImageErrorReload(context: Context, url: String?, fallbackUrl: String, imageView: ImageView)


    /**
     * 未来 Glide 将默认加载硬件位图而不需要额外的启用配置，只保留禁用的选项 现在已经默认开启了这个配置，但是在有些情况下需要关闭
     * 所以提供了以下的方法，禁用硬件位图 disallowHardwareConfig
     */
    //    哪些情况不能使用硬件位图?
    //    在显存中存储像素数据意味着这些数据不容易访问到，在某些情况下可能会发生异常。已知的情形列举如下：
    //    在 Java 中读写像素数据，包括：
    //    Bitmap#getPixel
    //    Bitmap#getPixels
    //    Bitmap#copyPixelsToBuffer
    //    Bitmap#copyPixelsFromBuffer
    //    在本地 (native) 代码中读写像素数据
    //    使用软件画布 (software Canvas) 渲染硬件位图:
    //    Canvas canvas = new Canvas(normalBitmap)
    //canvas.drawBitmap(hardwareBitmap, 0, 0, new Paint());
    //    在绘制位图的 View 上使用软件层 (software layer type) （例如，绘制阴影）
    //    ImageView imageView = …
    //            imageView.setImageBitmap(hardwareBitmap);
    //imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    //    打开过多的文件描述符 . 每个硬件位图会消耗一个文件描述符。
    // 这里存在一个每个进程的文件描述符限制 ( Android O 及更早版本一般为 1024，在某些 O-MR1 和更高的构建上是 32K)。
    // Glide 将尝试限制分配的硬件位图以保持在这个限制以内，但如果你已经分配了大量的文件描述符，这可能是一个问题。
    //    需要ARGB_8888 Bitmaps 作为前置条件
    //    在代码中触发截屏操作，它会尝试使用 Canvas 来绘制视图层级。
    //    作为一个替代方案，在 Android O 以上版本你可以使用 PixelCopy.
    //   共享元素过渡 (shared element transition)(OMR1已修复)
    fun disPlayImagedisallowHardwareConfig(fragment: Fragment, url: String?, imageView: ImageView)

    fun disPlayImagedisallowHardwareConfig(activity: Activity, url: String?, imageView: ImageView)

    fun disPlayImagedisallowHardwareConfig(context: Context, url: String?, imageView: ImageView)

    //监听图片的下载进度，是否完成，百分比 也可以加载本地图片，扩张一下
    fun disPlayImageProgress(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener)

    fun disPlayImageProgress(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener)

    fun disPlayImageProgress(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener)

    fun disPlayImageProgressByOnProgressListener(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener)

    fun disPlayImageProgressByOnProgressListener(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener)

    fun disPlayImageProgressByOnProgressListener(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener)


    //    TransitionOptions 用于给一个特定的请求指定过渡。
    //    每个请求可以使用 RequestBuilder 中的 transition()
    //    方法来设定 TransitionOptions 。还可以通过使用
    //    BitmapTransitionOptions 或 DrawableTransitionOptions
    //    来指定类型特定的过渡动画。对于 Bitmap 和 Drawable
    //    之外的资源类型，可以使用 GenericTransitionOptions。 Glide v4 将不会默认应用交叉淡入或任何其他的过渡效果。每个请求必须手动应用过渡。
    fun displayImageByTransition(context: Context, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView)

    fun displayImageByTransition(activity: Activity, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView)

    fun displayImageByTransition(fragment: Fragment, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView)

    //失去焦点，建议实际的项目中少用，取消求情
    fun glidePauseRequests(context: Context)

    fun glidePauseRequests(activity: Activity)

    fun glidePauseRequests(fragment: Fragment)

    //获取焦点，建议实际的项目中少用
    fun glideResumeRequests(context: Context)

    fun glideResumeRequests(activity: Activity)

    fun glideResumeRequests(fragment: Fragment)

    //加载缩图图     int thumbnailSize = 10;//越小，图片越小，低网络的情况，图片越小
    //GlideApp.with(this).load(urlnoData).override(thumbnailSize))// API 来强制 Glide 在缩略图请求中加载一个低分辨率图像
    fun displayImageThumbnail(context: Context, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView)

    fun displayImageThumbnail(activity: Activity, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView)

    fun displayImageThumbnail(fragment: Fragment, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView)

    //如果没有两个url的话，也想，记载一个缩略图
    fun displayImageThumbnail(fragment: Fragment, url: String?, thumbnailSize: Float, imageView: ImageView)

    fun displayImageThumbnail(activity: Activity, url: String?, thumbnailSize: Float, imageView: ImageView)

    fun displayImageThumbnail(context: Context, url: String?, thumbnailSize: Float, imageView: ImageView)
}
