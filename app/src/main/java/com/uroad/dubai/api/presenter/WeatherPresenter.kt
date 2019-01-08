package com.uroad.dubai.api.presenter

import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.WeatherView
import com.uroad.dubai.model.WeatherMDL

class WeatherPresenter(val weatherView : WeatherView) : BasePresenter<WeatherView>(weatherView) {
    fun getNewsWeatherData(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(weatherView) {
            override fun onHttpResultOk(data: String?) {
                weatherView.onGetNewList(GsonUtils.fromDataBean(data, WeatherMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                weatherView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}