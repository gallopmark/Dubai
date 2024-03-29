package com.uroad.dubai.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.utils.DisplayUtils


class ViewHistoryListCardAdapter(context: Context, data: MutableList<ScenicMDL>)
    : BaseArrayRecyclerAdapter<ScenicMDL>(context, data) {

    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val color66 = ContextCompat.getColor(context, R.color.color_66)

    override fun bindView(viewType: Int): Int = R.layout.item_viewhistorylist_card

    override fun onBindHoder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
        holder.setText(R.id.tvTitle, t.title)
        //holder.setText(R.id.tvLocation, getLocation(t.address))
        holder.setText(R.id.tvHours, t.content)
        holder.setText(R.id.tvLocation, t.address)
        holder.setText(R.id.tvDistance, "${t.commentstar}km")
    }

    private fun getLocation(address: String?): SpannableString {
        var location = "Location\u2000"
        val end = location.length
        address?.let { location += it }
        return SpannableString(location).apply { setSpan(ForegroundColorSpan(color66), 0, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }
}