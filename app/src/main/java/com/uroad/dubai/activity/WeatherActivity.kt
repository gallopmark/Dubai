package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity

class WeatherActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        setBaseContentView(R.layout.activity_weather, true)
    }

}