package com.uroad.dubai.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.model.CalendarMDL
import com.uroad.library.widget.banner.BaseBannerAdapter

class TravelCalendarAdapter(context: Context, data: MutableList<CalendarMDL>)
    : BaseBannerAdapter<CalendarMDL>(context, data) {

    override fun convert(mConvertView: View, item: CalendarMDL, realPosition: Int) {
        val tvTitleTime = mConvertView.findViewById<TextView>(R.id.tvCalendar)
        val tvAM = mConvertView.findViewById<TextView>(R.id.tvAM)
        val tvPM = mConvertView.findViewById<TextView>(R.id.tvPM)
        val tvAMContent = mConvertView.findViewById<TextView>(R.id.tvAMContent)
        val tvPMContent = mConvertView.findViewById<TextView>(R.id.tvPMContent)

        tvTitleTime.text = item.weekDataTitle?.replace("/",
                        " ")?.replaceFirst(" ","/")?:""
        val list = item.list
        list?.let {
            when {
                it.size>=2 -> {
                    tvAM.text = getStr(it[0].dtstart ?:"",it[0].dtend?:"")
                    tvAMContent.text = it[0].title?:""
                    tvPM.text = getStr(it[1].dtstart?:"",it[1].dtend?:"")
                    tvPMContent.text = it[1].title?:""
                }
                it.size == 1 -> {
                    tvAM.text = getStr(it[0].dtstart ?:"",it[0].dtend?:"")
                    tvAMContent.text = it[0].title?:""
                    tvPM.text = ""
                    tvPMContent.text = ""
                }
                else -> {
                    tvAM.text = ""
                    tvAMContent.text = ""
                    tvPM.text = ""
                    tvPMContent.text = ""
                }
            }

        }

    }

    override fun bindView(viewType: Int): Int = R.layout.item_favorites_banner


    private fun getStr(dtstart: String?, dtend: String?):String{
        return "${dtstart?.substring(10,dtstart.length-3)}-${dtend?.substring(10,dtend.length-3)}".trim()
    }

}