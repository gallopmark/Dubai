package com.uroad.dubai.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.utils.DisplayUtils


class AttractionListCardAdapter(context: Context, data: MutableList<ScenicMDL>, private val type: Int)
    : BaseArrayRecyclerAdapter<ScenicMDL>(context, data) {
    private var imageWith = DisplayUtils.getWindowWidth(context) / 3
    private var imageHeight = imageWith * 3 / 4
    private var params = RelativeLayout.LayoutParams(imageWith, imageHeight).apply { addRule(RelativeLayout.CENTER_VERTICAL) }
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val color66 = ContextCompat.getColor(context, R.color.color_66)

    override fun bindView(viewType: Int): Int = if (type == 1)
        R.layout.item_attractionlist_card else R.layout.item_attraction_card

    override fun onBindHoder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        if (type == 1) {
            holder.setLayoutParams(R.id.ivPic, params)
            holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvLocation, getLocation(t.address))
            holder.setText(R.id.tvHours, getHours(t.hours))
        } else {
            holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7)
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            holder.setText(R.id.tvAddress, t.address)
            holder.setText(R.id.tvDistance, t.distance)
        }
    }

    private fun getLocation(address: String?): SpannableString {
        var location = "Location\u2000"
        val end = location.length
        address?.let { location += it }
        return SpannableString(location).apply { setSpan(ForegroundColorSpan(color66), 0, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }

    private fun getHours(hours: String?): SpannableString {
        var times = "Hours\u2000"
        val end = times.length
        hours?.let { times += it }
        return SpannableString(times).apply { setSpan(ForegroundColorSpan(color66), 0, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }
}