package com.uroad.dubai.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.utils.DisplayUtils


class ViewHistoryRoadsListCardAdapter(context: Context, data: MutableList<ScenicMDL>)
    : BaseArrayRecyclerAdapter<ScenicMDL>(context, data) {

    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val color66 = ContextCompat.getColor(context, R.color.color_66)

    override fun bindView(viewType: Int): Int = R.layout.item_viewhistorylist_roads_card

    override fun onBindHoder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        holder.setText(R.id.tvTitleTime,"Staturday,Dec.22")
        holder.setText(R.id.tvStartRoads,"Dubai International Airport")
        holder.setText(R.id.tvEndRoads,"Holiday Inn Express Dubai Airport")
        holder.setText(R.id.tvTime,"5mind")
    }

    private fun getLocation(address: String?): SpannableString {
        var location = "Location\u2000"
        val end = location.length
        address?.let { location += it }
        return SpannableString(location).apply { setSpan(ForegroundColorSpan(color66), 0, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }
}