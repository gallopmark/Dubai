package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.adapter.WeatherAdapter
import com.uroad.dubai.api.presenter.WeatherPresenter
import com.uroad.dubai.api.view.WeatherView
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.model.WeatherMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_weather.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : BaseActivity() ,WeatherView {

    private lateinit var data: MutableList<WeatherMDL.DailyForecastsMDL>
    private lateinit var adapter : WeatherAdapter
    var presenter : WeatherPresenter? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        //withTitle("08/11/Monday")
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        withOption(drawable(R.mipmap.icon_clock), "${repair(hour)}:${repair(minute)}",6,
                onClickListener = View.OnClickListener {})
        setBaseContentView(R.layout.activity_weather,true)
    }

    init {
        presenter = WeatherPresenter(this)
    }

    override fun initData() {
        data = ArrayList()
        adapter = WeatherAdapter(this,data)
        presenter?.getNewsWeatherData(WebApi.GET_WEATHER_LIST,WebApi.getWeatherListParams())

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        ryWeather.layoutManager = layoutManager
        ryWeather.adapter = adapter

        val mdl = WeatherMDL.DailyForecastsMDL()
        data.add(mdl)
        data.add(mdl)
        data.add(mdl)
        data.add(mdl)
        data.add(mdl)
        data.add(mdl)
        data.add(mdl)
    }

    @SuppressLint("LogNotTimber")
    override fun onGetNewList(mdl: WeatherMDL?) {
        mdl?.let {
            data.clear()
            mdl.DailyForecasts?.let {
                it1 -> data.addAll(it1)
                data.removeAt(0)

                val temperature = it.DailyForecasts?.get(0)?.Temperature
                val phrase = it.DailyForecasts?.get(0)?.Day?.IconPhrase
                val maximum = temperature?.Maximum
                //val minimum = temperature?.Minimum
                val wd = ((maximum?.Value!! /*+ minimum?.Value!!*/ -32)/1.8f )
                tvTemperature.text = changeDouble(wd).toString()
                tvTime.text = phrase
                ivWeather.setImageDrawable(drawable(R.mipmap.icon_cloudy))

            }
            adapter.notifyDataSetChanged()
            it.Headline?.EffectiveDate?.let {
                it1 -> val date = parseDate(it1)
                withTitle(getWeekOfDate(date))
            }
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }

    override fun onShowLoading() {}
    override fun onHideLoading() {}
    override fun onShowError(msg: String?) {}

    @SuppressLint("SimpleDateFormat")
    fun parseDate(dateStr:String) : String {
        val split = dateStr.split("T")
        return split[0]
    }


    //	实现给定某日期，判断是星期几
    /**
     * @param date
     * @return 当前日期是星期几
     */
    @SuppressLint("SimpleDateFormat")
    fun getWeekOfDate(date: String): String {
        val weekDays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val cal = Calendar.getInstance()
        cal.time = SimpleDateFormat("yyyy-MM-dd").parse(date)
        var w = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0)
            w = 0
        val split = date.split("-")
        return "${split[1]}/${split[2]}/${weekDays[w]}"
    }

    private fun changeDouble(dou: Double?): Double {
        var d = dou
        val nf = DecimalFormat("0.0 ")
        d = java.lang.Double.parseDouble(nf.format(d))
        return d
    }

    private fun repair(time : Int) : String{
        if(time < 10)
            return "0$time"
        return "$time"
    }

}