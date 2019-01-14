package com.uroad.dubai.photopicker.adapter

import android.content.Context
import android.support.v4.util.ArrayMap
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.photopicker.model.ImageItem

class ImageRvAdapter(context: Context, images: MutableList<ImageItem>)
    : BaseArrayRecyclerAdapter<ImageItem>(context, images) {
    private var selectIndex: Int = 0
    private val checkItems = ArrayMap<Int, Boolean>()

    init {
        for (i in 0 until mDatas.size) {
            checkItems[i] = true
        }
    }

    fun setSelectIndex(selectIndex: Int) {
        this.selectIndex = selectIndex
        notifyDataSetChanged()
    }

    fun setUnCheckItem(position: Int, isCheck: Boolean) {
        checkItems[position] = isCheck
        notifyDataSetChanged()
    }

    fun getUnCheckItems(): ArrayMap<Int, Boolean> {
        return checkItems
    }

    override fun onBindHoder(holder: RecyclerHolder, t: ImageItem, position: Int) {
        holder.displayImage(R.id.ivPic, t.path)
        if (position == selectIndex) {
            holder.setBackgroundResource(R.id.flSelect, R.drawable.bg_photopicker_corner_2dp)
        } else {
            holder.setBackgroundResource(R.id.flSelect, 0)
        }
        if (checkItems[position] == true) {
            holder.setVisibility(R.id.vUnSelect, View.GONE)
        } else {
            holder.setVisibility(R.id.vUnSelect, View.VISIBLE)
        }
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.item_photopicker_rvpic
    }
}