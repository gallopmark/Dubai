package com.uroad.dubai.photopicker.adapter

import android.app.Activity
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.photopicker.model.ImageItem
import com.uroad.glidev4.GlideV4
import com.uroad.glidev4.listener.ImageSize
import com.uroad.library.utils.DisplayUtils

class ImageGridAdapter(private val context: Activity,
                       data: MutableList<ImageItem>,
                       private val isMultiple: Boolean,
                       private val limit: Int)
    : BaseArrayRecyclerAdapter<ImageItem>(context, data) {
    companion object {
        private const val ITEM_IMAGE = 1
        private const val ITEM_CAMERA = 2
    }

    private val mImageSize: Int
    private var mSelects = ArrayList<ImageItem>()
    private var onImageItemClickListener: OnImageItemClickListener? = null

    init {
        val screenWidth = DisplayUtils.getWindowWidth(context)
        val columnSpace = DisplayUtils.dip2px(context, 2f)
        mImageSize = (screenWidth - columnSpace * 2) / 3
    }

    fun setSelects(images: MutableList<ImageItem>) {
        mSelects.clear()
        mSelects.addAll(images)
        notifyDataSetChanged()
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, t: ImageItem, position: Int) {
        val itemType = holder.itemViewType
        holder.itemView.layoutParams = FrameLayout.LayoutParams(mImageSize, mImageSize)
        if (itemType == ITEM_IMAGE) {
            val ivPic = holder.obtainView<ImageView>(R.id.ivPic)
            val checkBox = holder.obtainView<CheckBox>(R.id.checkBox)
            GlideV4.displayImage(context, t.path,
                    ivPic, R.color.darkgrey,
                    ImageSize(mImageSize, mImageSize))
            if (isMultiple) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if (isMultiple) {
                    if (checkBox.isChecked) {
                        mSelects.remove(t)
                        checkBox.isChecked = false
                    } else {
                        if (mSelects.contains(t)) {
                            mSelects.remove(t)
                        }
                        if (mSelects.size < limit) {
                            mSelects.add(t)
                            checkBox.isChecked = true
                        } else {
                            checkBox.isChecked = false
                            onImageItemClickListener?.onOverSelected(limit)
                        }
                    }
                } else {
                    mSelects.clear()
                    mSelects.add(t)
                }
                onImageItemClickListener?.onSelected(mSelects)
            }
            checkBox.setOnClickListener {
                if (mSelects.contains(t)) {
                    checkBox.isChecked = false
                    mSelects.remove(t)
                } else {
                    if (mSelects.size < limit) {
                        mSelects.add(t)
                        checkBox.isChecked = true
                    } else {
                        checkBox.isChecked = false
                        onImageItemClickListener?.onOverSelected(limit)
                    }
                }
                onImageItemClickListener?.onSelected(mSelects)
            }
            checkBox.isChecked = mSelects.contains(t)
        } else {
            holder.itemView.setOnClickListener { onImageItemClickListener?.onCamera() }
        }
    }

    override fun bindView(viewType: Int): Int {
        return if (viewType == ITEM_IMAGE) R.layout.item_photopicker_imagegrid
        else R.layout.item_photopicker_camera
    }

    override fun getItemViewType(position: Int): Int {
        return if (mDatas[position].showCamera) ITEM_CAMERA
        else ITEM_IMAGE
    }

    interface OnImageItemClickListener {
        fun onCamera()
        fun onOverSelected(limit: Int)
        fun onSelected(mDatas: ArrayList<ImageItem>)
    }

    fun setOnImageItemClickListener(onImageItemClickListener: OnImageItemClickListener) {
        this.onImageItemClickListener = onImageItemClickListener
    }
}