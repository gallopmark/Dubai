package com.uroad.dubai.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.model.FavoritesMDL
import com.uroad.library.widget.banner.BaseBannerAdapter

class TravelCalendarAdapter(context: Context, data: MutableList<FavoritesMDL>)
    : BaseBannerAdapter<FavoritesMDL>(context, data) {

    override fun convert(mConvertView: View, item: FavoritesMDL, realPosition: Int) {
        val tvTitleTime = mConvertView.findViewById<TextView>(R.id.tvTitleTime)
        val tvAM = mConvertView.findViewById<TextView>(R.id.tvAM)
        val tvPM = mConvertView.findViewById<TextView>(R.id.tvPM)
        val tvAMContent = mConvertView.findViewById<TextView>(R.id.tvAMContent)
        val tvPMContent = mConvertView.findViewById<TextView>(R.id.tvPMContent)
        tvTitleTime.text = item.calendarMDL?.titleTimeData?:""
        tvAM.text = getStr(item.calendarMDL?.dtstart?:"")
        tvPM.text = getStr(item.calendarMDL?.dtend?:"")
        tvAMContent.text = item.calendarMDL?.title?:""
        tvPMContent.text = item.calendarMDL?.description?:""
    }

    override fun bindView(viewType: Int): Int = R.layout.item_favorites_banner

   /* override fun bindHolder(holder: RecyclerHolder, t: FavoritesMDL, position: Int) {
        holder.setText(R.id.tvTitleTime,t.calendarMDL?.titleTimeData?:"")
        holder.setText(R.id.tvAM,getStr(t.calendarMDL?.dtstart?:""))
        holder.setText(R.id.tvPM,getStr(t.calendarMDL?.dtend?:""))
        holder.setText(R.id.tvAMContent,t.calendarMDL?.title?:"")
        holder.setText(R.id.tvPMContent,t.calendarMDL?.description?:"")
    }*/

    private fun getStr(dtstart: String?):String{
        return "${dtstart?.substring(10,dtstart.length-3)} ${s(dtstart)}"
    }
    private fun s(dtstart: String?):String{
        val i = dtstart?.substring(11, 13)?.trim()?.toInt()
        if (i != null) {
            if (i>=12)
                return "pm"
        }
        return "am"
    }
}