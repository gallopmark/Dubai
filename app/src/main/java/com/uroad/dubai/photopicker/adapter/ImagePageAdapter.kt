package com.uroad.dubai.photopicker.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.uroad.dubai.photopicker.model.ImageItem
import com.uroad.dubai.photopicker.utils.ImageUtil
import com.uroad.glidev4.GlideV4


class ImagePageAdapter(private val context: Context, private val images: ArrayList<ImageItem>) : PagerAdapter() {

    private var mListener: OnItemClickListener? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val photoView = PhotoView(context)
        photoView.adjustViewBounds = true
        container.addView(photoView)
        GlideV4.getInstance().getBitmapFromCache(context, images[position].path) { resource ->
            resource?.let {
                val bw = resource.width
                val bh = resource.height
                if (bw > 8192 || bh > 8192) {
                    val bitmap = ImageUtil.zoomBitmap(resource, 8192, 8192)
                    setBitmap(photoView, bitmap)
                } else {
                    setBitmap(photoView, resource)
                }
            }
        }
        photoView.setOnClickListener { mListener?.onItemClick(position, images[position]) }
        return photoView
    }

    private fun setBitmap(imageView: PhotoView, bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
        bitmap?.let {
            val bw = it.width
            val bh = it.height
            val vw = imageView.width
            val vh = imageView.height
            if (bw != 0 && bh != 0 && vw != 0 && vh != 0) {
                if (1.0f * bh / bw > 1.0f * vh / vw) {
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is PhotoView) {
            Glide.with(context).clear(`object`)
            `object`.setImageDrawable(null)
            container.removeView(`object`)
        }
    }

    fun setOnItemClickListener(l: OnItemClickListener) {
        mListener = l
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, path: ImageItem)
    }
}