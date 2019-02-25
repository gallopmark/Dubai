package com.uroad.glidev4

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.uroad.glidev4.listener.IGetBitmapListener
import com.uroad.glidev4.listener.IGetDrawableListener
import com.uroad.glidev4.listener.IImageLoaderListener
import com.uroad.glidev4.listener.ImageSize
import com.uroad.glidev4.listener.OnGlideImageViewListener
import com.uroad.glidev4.listener.OnProgressListener
import com.uroad.glidev4.util.GlideUtil
import java.io.File

class GlideImageLoaderClient : IImageLoaderClient {
    override fun init(context: Context) {

    }

    override fun destroy(context: Context) {
        clearMemoryCache(context)
    }

    override fun getCacheDir(context: Context): File? {
        return Glide.getPhotoCacheDir(context)
    }

    @UiThread
    override fun clearMemoryCache(context: Context) {
        Glide.get(context).clearMemory()
    }

    override fun clearDiskCache(context: Context) {
        Thread(Runnable { Glide.get(context).clearDiskCache() }).start()
    }

    override fun getBitmapFromCache(context: Context, url: String?): Bitmap? {
        throw UnsupportedOperationException("glide 不支持同步 获取缓存中 bitmap")
    }

    @SuppressLint("CheckResult")
    override fun getBitmapFromCache(context: Context, url: String?, listener: IGetBitmapListener) {
        Glide.with(context).asBitmap().load(url).listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                listener.onBitmap(resource)
                return false
            }
        })
    }

    /**
     * 默认的策略是DiskCacheStrategy.AUTOMATIC
     * DiskCacheStrategy.ALL 使用DATA和RESOURCE缓存远程数据，仅使用RESOURCE来缓存本地数据。
     * DiskCacheStrategy.NONE 不使用磁盘缓存
     * DiskCacheStrategy.DATA 在资源解码前就将原始数据写入磁盘缓存
     * DiskCacheStrategy.RESOURCE 在资源解码后将数据写入磁盘缓存，即经过缩放等转换后的图片资源。
     * DiskCacheStrategy.AUTOMATIC 根据原始图片数据和资源编码策略来自动选择磁盘缓存策略。
     *
     * @param context   上下文
     * @param resId     id
     * @param imageView into
     */
    override fun displayImage(context: Context, resId: Int, imageView: ImageView) {
        //设置缓存策略缓存原始数据  Saves just the original data to cache
        Glide.with(context).load(resId)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView) {
        GlideUtil.displayImage(context, url, imageView)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView) {
        GlideUtil.displayImage(fragment, url, imageView)
    }

    /**
     * @param isCache 是否是缓存 如果是：缓存策略缓存原始数据  不是的话 ：缓存策略DiskCacheStrategy.NONE：什么都不缓存
     */
    override fun displayImage(context: Context, url: String?, imageView: ImageView, isCache: Boolean) {
        Glide.with(context).load(url)
                .apply(RequestOptions().skipMemoryCache(isCache).diskCacheStrategy(if (isCache) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE))
                .into(imageView)
    }

    /**
     * 使用.placeholder()方法在某些情况下会导致图片显示的时候出现图片变形的情况
     * 这是因为Glide默认开启的crossFade动画导致的TransitionDrawable绘制异常
     *
     * @param defRes defRes 可以是个new ColorDrawable(Color.BLACK) 也可以是张图片
     */
    //默认为200  时间有点长  ，工程中要修改下，设置一个加载失败和加载中的动画过渡，V4.0的使用的方法
    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int) {
        GlideUtil.displayImage(context, url, imageView, defRes)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int) {
        GlideUtil.displayImage(fragment, url, imageView, defRes)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        GlideUtil.displayImage(context, url, imageView, defRes, transformations)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        GlideUtil.displayImage(fragment, url, imageView, defRes, transformations)
    }


    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, size: ImageSize) {
        GlideUtil.displayImage(context, url, imageView, defRes, size)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, size: ImageSize) {
        GlideUtil.displayImage(fragment, url, imageView, defRes, size)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, size: ImageSize) {
        GlideUtil.displayImage(context, url, imageView, size)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, size: ImageSize) {
        GlideUtil.displayImage(fragment, url, imageView, size)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean) {
        GlideUtil.displayImage(context, url, imageView, defRes, cacheInMemory)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, cacheInMemory: Boolean) {
        GlideUtil.displayImage(fragment, url, imageView, defRes, cacheInMemory)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, listener: IImageLoaderListener) {
        GlideUtil.displayImage(context, url, imageView, listener)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, listener: IImageLoaderListener) {
        GlideUtil.displayImage(fragment, url, imageView, listener)
    }

    override fun displayImage(context: Context, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener) {
        GlideUtil.displayImage(context, url, imageView, defRes, listener)
    }

    override fun displayImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, listener: IImageLoaderListener) {
        GlideUtil.displayImage(fragment, url, imageView, defRes, listener)
    }

    override fun displayCircleImage(context: Context, url: String?, imageView: ImageView, defRes: Int) {
        GlideUtil.displayCircleImage(context, url, imageView, defRes)
    }

    override fun displayCircleImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int) {
        GlideUtil.displayCircleImage(fragment, url, imageView, defRes)
    }

    override fun displayRoundImage(context: Context, url: String?, imageView: ImageView, defRes: Int, radius: Int) {
        GlideUtil.displayRoundImage(context, url, imageView, defRes, radius)
    }

    override fun displayRoundImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, radius: Int) {
        GlideUtil.displayRoundImage(fragment, url, imageView, defRes, radius)
    }

    /**
     * @param blurRadius 模糊的程度 ，数字越大越模糊
     * @param listener   接口回调需要拿到drawable
     */
    @SuppressLint("CheckResult")
    override fun displayBlurImage(context: Context, url: String?, blurRadius: Int, listener: IGetDrawableListener) {
        GlideUtil.displayBlurImage(context, url, blurRadius, listener)
    }

    override fun displayBlurImage(context: Context, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int) {
        GlideUtil.displayBlurImage(context, url, imageView, defRes, blurRadius)
    }

    override fun displayBlurImage(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, blurRadius: Int) {
        GlideUtil.displayBlurImage(fragment, url, imageView, defRes, blurRadius)
    }

    override fun displayBlurImage(context: Context, resId: Int, imageView: ImageView, blurRadius: Int) {
        GlideUtil.displayBlurImage(context, resId, imageView, blurRadius)
    }

    /**
     * 加载资源文件
     */
    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView) {
        GlideUtil.displayImageInResource(context, resId, imageView)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView) {
        GlideUtil.displayImageInResource(fragment, resId, imageView)
    }

    /**
     * 加载资源文件的同时，对图片进行处理
     */
    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, transformations: BitmapTransformation) {
        GlideUtil.displayImageInResource(context, resId, imageView, transformations)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, transformations: BitmapTransformation) {
        GlideUtil.displayImageInResource(fragment, resId, imageView, transformations)
    }

    /**
     * 加载资源文件失败了，加载中的默认图和失败的图片
     */
    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int) {
        GlideUtil.displayImageInResource(context, resId, imageView, defRes)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int) {
        GlideUtil.displayImageInResource(fragment, resId, imageView, defRes)
    }

    override fun displayImageInResource(context: Context, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        GlideUtil.displayImageInResource(context, resId, imageView, defRes, transformations)
    }

    override fun displayImageInResource(fragment: Fragment, resId: Int, imageView: ImageView, defRes: Int, transformations: BitmapTransformation) {
        GlideUtil.displayImageInResource(fragment, resId, imageView, defRes, transformations)
    }

    override fun displayImageInResourceTransform(activity: Activity, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        GlideUtil.displayImageInResourceTransform(activity, resId, imageView, transformation, errorResId)
    }

    override fun displayImageInResourceTransform(context: Context, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        GlideUtil.displayImageInResourceTransform(context, resId, imageView, transformation, errorResId)
    }

    override fun displayImageInResourceTransform(fragment: Fragment, resId: Int, imageView: ImageView, transformation: BitmapTransformation, errorResId: Int) {
        GlideUtil.displayImageInResourceTransform(fragment, resId, imageView, transformation, errorResId)
    }

    override fun displayImageByNet(context: Context, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        GlideUtil.displayImageByNet(context, url, imageView, defRes, transformation)
    }

    override fun displayImageByNet(fragment: Fragment, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        GlideUtil.displayImageByNet(fragment, url, imageView, defRes, transformation)
    }

    override fun displayImageByNet(activity: Activity, url: String?, imageView: ImageView, defRes: Int, transformation: BitmapTransformation) {
        GlideUtil.displayImageByNet(activity, url, imageView, defRes, transformation)
    }

    override fun clear(activity: Activity, imageView: ImageView) {
        Glide.with(activity).clear(imageView)
    }

    override fun clear(context: Context, imageView: ImageView) {
        Glide.with(context).clear(imageView)
    }

    override fun clear(fragment: Fragment, imageView: ImageView) {
        Glide.with(fragment).clear(imageView)
    }

    //    默认的策略是DiskCacheStrategy.AUTOMATIC
//    DiskCacheStrategy.ALL 使用DATA和RESOURCE缓存远程数据，仅使用RESOURCE来缓存本地数据。
//    DiskCacheStrategy.NONE 不使用磁盘缓存
//    DiskCacheStrategy.DATA 在资源解码前就将原始数据写入磁盘缓存
//    DiskCacheStrategy.RESOURCE 在资源解码后将数据写入磁盘缓存，即经过缩放等转换后的图片资源。
//    DiskCacheStrategy.AUTOMATIC 根据原始图片数据和资源编码策略来自动选择磁盘缓存策略。
    override fun displayImageByDiskCacheStrategy(fragment: Fragment, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        GlideUtil.displayImageByDiskCacheStrategy(fragment, url, diskCacheStrategy, imageView)
    }

    //    DiskCacheStrategy.NONE： 表示不缓存任何内容。
//    DiskCacheStrategy.DATA： 表示只缓存原始图片。
//    DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
//    DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
//    DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。
    override fun displayImageByDiskCacheStrategy(activity: Activity, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        GlideUtil.displayImageByDiskCacheStrategy(activity, url, diskCacheStrategy, imageView)
    }

    override fun displayImageByDiskCacheStrategy(context: Context, url: String?, diskCacheStrategy: DiskCacheStrategy, imageView: ImageView) {
        GlideUtil.displayImageByDiskCacheStrategy(context, url, diskCacheStrategy, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(fragment: Fragment, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageOnlyRetrieveFromCache(fragment, url, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(activity: Activity, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageOnlyRetrieveFromCache(activity, url, imageView)
    }

    override fun disPlayImageOnlyRetrieveFromCache(context: Context, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageOnlyRetrieveFromCache(context, url, imageView)
    }

    /**
     * 如果你想确保一个特定的请求跳过磁盘和/或内存缓存（比如，图片验证码 –）
     *
     * @param skipflag         是否跳过内存缓存
     * @param diskCacheStratey 是否跳过磁盘缓存
     */
    override fun disPlayImageSkipMemoryCache(fragment: Fragment, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        GlideUtil.disPlayImageSkipMemoryCache(fragment, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageSkipMemoryCache(activity: Activity, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        GlideUtil.disPlayImageSkipMemoryCache(activity, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageSkipMemoryCache(context: Context, url: String?, imageView: ImageView, skipflag: Boolean, diskCacheStratey: Boolean) {
        GlideUtil.disPlayImageSkipMemoryCache(context, url, imageView, skipflag, diskCacheStratey)
    }

    override fun disPlayImageErrorReload(fragment: Fragment, url: String?, fallbackUrl: String, imageView: ImageView) {
        GlideUtil.disPlayImageErrorReload(fragment, url, fallbackUrl, imageView)
    }

    override fun disPlayImageErrorReload(activity: Activity, url: String?, fallbackUrl: String, imageView: ImageView) {
        GlideUtil.disPlayImageErrorReload(activity, url, fallbackUrl, imageView)
    }

    override fun disPlayImageErrorReload(context: Context, url: String?, fallbackUrl: String, imageView: ImageView) {
        GlideUtil.disPlayImageErrorReload(context, url, fallbackUrl, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(fragment: Fragment, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageDisallowHardwareConfig(fragment, url, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(activity: Activity, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageDisallowHardwareConfig(activity, url, imageView)
    }

    override fun disPlayImagedisallowHardwareConfig(context: Context, url: String?, imageView: ImageView) {
        GlideUtil.disPlayImageDisallowHardwareConfig(context, url, imageView)
    }

    //监听进度
    override fun disPlayImageProgress(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        disPlayImageInProgress(context, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgress(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        disPlayImageInProgress(activity, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgress(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        disPlayImageInProgress(fragment, url, imageView, placeholderResId, errorResId, listener)
    }

    override fun disPlayImageProgressByOnProgressListener(context: Context, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        disPlayImageProgressByProgressListener(context, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    override fun disPlayImageProgressByOnProgressListener(activity: Activity, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        disPlayImageProgressByProgressListener(activity, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    override fun disPlayImageProgressByOnProgressListener(fragment: Fragment, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        disPlayImageProgressByProgressListener(fragment, url, imageView, placeholderResId, errorResId, onProgressListener)
    }

    private fun disPlayImageInProgress(`object`: Any, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, listener: OnGlideImageViewListener) {
        val manager = GlideUtil.withRequestManager(`object`) ?: return
        manager.load(url).apply(RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderResId)
                .error(errorResId))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        mainThreadCallback(url, mLastBytesRead, mTotalBytes, true, e)
                        ProgressManager.removeProgressListener(internalProgressListener)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        mainThreadCallback(url, mLastBytesRead, mTotalBytes, true, null)
                        ProgressManager.removeProgressListener(internalProgressListener)
                        return false
                    }
                }).into(imageView)
        //赋值 上去
        onGlideImageViewListener = listener
        mMainThreadHandler = Handler(Looper.getMainLooper())
        internalProgressListener = object : OnProgressListener {
            override fun onProgress(imageUrl: String?, bytesRead: Long, totalBytes: Long, isDone: Boolean, exception: GlideException?) {
                if (totalBytes == 0L) return
                if (mLastBytesRead == bytesRead && mLastStatus == isDone) return

                mLastBytesRead = bytesRead
                mTotalBytes = totalBytes
                mLastStatus = isDone
                mainThreadCallback(imageUrl, bytesRead, totalBytes, isDone, exception)

                if (isDone) {
                    ProgressManager.removeProgressListener(this)
                }
            }
        }
        ProgressManager.addProgressListener(internalProgressListener)
    }

    private fun disPlayImageProgressByProgressListener(`object`: Any, url: String?, imageView: ImageView, placeholderResId: Int, errorResId: Int, onProgressListener: OnProgressListener) {
        val manager = GlideUtil.withRequestManager(`object`) ?: return
        manager.load(url).apply(RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderResId)
                .error(errorResId))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        mainThreadCallback(url, mLastBytesRead, mTotalBytes, true, e)
                        ProgressManager.removeProgressListener(internalProgressListener)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        mainThreadCallback(url, mLastBytesRead, mTotalBytes, true, null)
                        ProgressManager.removeProgressListener(internalProgressListener)
                        return false
                    }
                }).into(imageView)
        //赋值 上去
        this.onProgressListener = onProgressListener
        mMainThreadHandler = Handler(Looper.getMainLooper())
        internalProgressListener = object : OnProgressListener {
            override fun onProgress(imageUrl: String?, bytesRead: Long, totalBytes: Long, isDone: Boolean, exception: GlideException?) {
                if (totalBytes == 0L) return
                if (mLastBytesRead == bytesRead && mLastStatus == isDone) return
                mLastBytesRead = bytesRead
                mTotalBytes = totalBytes
                mLastStatus = isDone
                mainThreadCallback(imageUrl, bytesRead, totalBytes, isDone, exception)
                if (isDone) {
                    ProgressManager.removeProgressListener(this)
                }
            }
        }
        ProgressManager.addProgressListener(internalProgressListener)
    }

    override fun displayImageByTransition(context: Context, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        GlideUtil.displayImageByTransition(context, url, transitionOptions, imageView)
    }

    override fun displayImageByTransition(activity: Activity, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        GlideUtil.displayImageByTransition(activity, url, transitionOptions, imageView)
    }

    override fun displayImageByTransition(fragment: Fragment, url: String?, transitionOptions: TransitionOptions<*, *>, imageView: ImageView) {
        GlideUtil.displayImageByTransition(fragment, url, transitionOptions, imageView)
    }

    override fun glidePauseRequests(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun glidePauseRequests(activity: Activity) {
        Glide.with(activity).pauseRequests()
    }

    override fun glidePauseRequests(fragment: Fragment) {
        Glide.with(fragment).pauseRequests()
    }

    override fun glideResumeRequests(context: Context) {
        Glide.with(context).resumeRequests()
    }

    override fun glideResumeRequests(activity: Activity) {
        Glide.with(activity).resumeRequests()
    }

    override fun glideResumeRequests(fragment: Fragment) {
        Glide.with(fragment).resumeRequests()
    }

    /**
     * 加载缩略图
     *
     * @param url           图片url
     * @param backUrl       缩略图的url
     * @param thumbnailSize 如果需要放大放小的数值
     */
    override fun displayImageThumbnail(context: Context, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(context, url, backUrl, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(activity: Activity, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(activity, url, backUrl, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(fragment: Fragment, url: String?, backUrl: String, thumbnailSize: Int, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(fragment, url, backUrl, thumbnailSize, imageView)
    }

    /**
     * * thumbnail 方法有一个简化版本，它只需要一个 sizeMultiplier 参数。
     * 如果你只是想为你的加载相同的图片，但尺寸为 View 或 Target 的某个百分比的话特别有用：
     */
    override fun displayImageThumbnail(fragment: Fragment, url: String?, thumbnailSize: Float, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(fragment,url, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(activity: Activity, url: String?, thumbnailSize: Float, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(activity,url, thumbnailSize, imageView)
    }

    override fun displayImageThumbnail(context: Context, url: String?, thumbnailSize: Float, imageView: ImageView) {
        GlideUtil.displayImageThumbnail(context,url, thumbnailSize, imageView)
    }

    private var mMainThreadHandler: Handler? = null
    private var mTotalBytes: Long = 0
    private var mLastBytesRead: Long = 0

    private fun mainThreadCallback(url: String?, bytesRead: Long, totalBytes: Long, isDone: Boolean, exception: GlideException?) {
        mMainThreadHandler?.post {
            SystemClock.sleep(100)
            val percent = (bytesRead * 1.0f / totalBytes * 100.0f).toInt()
            onProgressListener?.onProgress(url, bytesRead, totalBytes, isDone, exception)
            onGlideImageViewListener?.onProgress(percent, isDone, exception)
        }
    }

    private var mLastStatus = false
    private var internalProgressListener: OnProgressListener? = null
    private var onGlideImageViewListener: OnGlideImageViewListener? = null
    private var onProgressListener: OnProgressListener? = null

}
