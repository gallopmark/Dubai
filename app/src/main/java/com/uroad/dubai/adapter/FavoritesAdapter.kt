package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.model.FavoritesMDL
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter

class FavoritesAdapter(context: Context, data: MutableList<FavoritesMDL>)
    : BannerBaseArrayAdapter<FavoritesMDL>(context, data) {

    override fun bindView(viewType: Int): Int = R.layout.item_favorites_banner

    override fun bindHolder(holder: RecyclerHolder, t: FavoritesMDL, position: Int) {
        holder.setText(R.id.tvTitleTime,t.calendarMDL?.titleTimeData?:"")
        holder.setText(R.id.tvAM,getStr(t.calendarMDL?.dtstart?:""))
        holder.setText(R.id.tvPM,getStr(t.calendarMDL?.dtend?:""))
        holder.setText(R.id.tvAMContent,t.calendarMDL?.title?:"")
        holder.setText(R.id.tvPMContent,t.calendarMDL?.description?:"")
    }

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