package com.uroad.dubai.photopicker.adapter

import android.content.Context
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.photopicker.model.ImageFolder

class ImageFolderAdapter(context: Context, mDatas: MutableList<ImageFolder>)
    : BaseArrayRecyclerAdapter<ImageFolder>(context, mDatas) {
    private val frame = context.getString(R.string.photopicker_frame)
    private var mSelectItem: Int = 0

    fun setSelectedItem(position: Int) {
        mSelectItem = position
        notifyDataSetChanged()
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.item_photopicker_imagefolder
    }

    override fun onBindHoder(holder: RecyclerHolder, t: ImageFolder, position: Int) {
        holder.displayImage(R.id.ivImage, t.firstImagePath, R.color.translucence)
        holder.setText(R.id.tvName, t.name)
        holder.setText(R.id.tvCount, "${t.mediaItems.size}$frame")
        if (mSelectItem == position) {
            holder.setVisibility(R.id.ivSelect, View.VISIBLE)
        } else {
            holder.setVisibility(R.id.ivSelect, View.GONE)
        }
    }
}