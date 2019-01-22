package com.uroad.dubai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.WeatherMDL
import com.uroad.library.utils.DisplayUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter (private val context: Context, data: MutableList<WeatherMDL.DailyForecastsMDL>)
    : BaseArrayRecyclerAdapter<WeatherMDL.DailyForecastsMDL>(context,data) {

    private var itemWith =  DisplayUtils.getWindowWidth(context) / 4
    private var itemHeight = itemWith*2
    private var params = RelativeLayout.LayoutParams(itemWith,itemHeight).apply { addRule(RelativeLayout.CENTER_HORIZONTAL) }
    override fun onBindHoder(holder: RecyclerHolder, t: WeatherMDL.DailyForecastsMDL, position: Int) {
        holder.itemView.layoutParams = params
        t?.let {
            it.Date?.let { it1 ->
                val date = parseDate(it1)
                holder.setText(R.id.tvDate,getWeekOfDate(date))
                holder.setVisibility(R.id.ivTemperatureType,true)
            }
            it.Temperature?.let { it1 ->
                val wd = (it1.Maximum?.Value!! - 32) / 1.8f
                holder.setText(R.id.tvTemperature,changeDouble(wd).toString())
            }
        }
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.item_weather
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDate(dateStr:String) : String {
        val split = dateStr.split("T")
        return split[0]
    }


    //	实现给定某日期，判断是星期几
    /**
     *
     * @param date
     * @return 当前日期是星期几
     */
    @SuppressLint("SimpleDateFormat")
    fun getWeekOfDate(date: String): String {
        val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat")
        val cal = Calendar.getInstance()
        cal.time = SimpleDateFormat("yyyy-MM-dd").parse(date)
        var w = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0)
            w = 0
        return weekDays[w]
    }

    private fun changeDouble(dou: Double?): Double {
        var d = dou
        val nf = DecimalFormat("0.0")
        d = java.lang.Double.parseDouble(nf.format(d))
        return d
    }
}