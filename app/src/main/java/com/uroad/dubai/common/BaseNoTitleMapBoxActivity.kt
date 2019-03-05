package com.uroad.dubai.common

import android.os.Bundle
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.R

abstract class BaseNoTitleMapBoxActivity : BaseMapBoxActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(setBaseMapBoxView(), true)
        initMapView(savedInstanceState)
    }
}