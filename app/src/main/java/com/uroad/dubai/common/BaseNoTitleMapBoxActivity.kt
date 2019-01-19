package com.uroad.dubai.common

import android.os.Bundle
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.R

abstract class BaseNoTitleMapBoxActivity : BaseMapBoxActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(setBaseMapBoxView(), true)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        onMapSetUp(savedInstanceState)
        mapView.getMapAsync { mapBoxMap ->
            mapBoxMap.setStyle(Style.MAPBOX_STREETS) {
                this@BaseNoTitleMapBoxActivity.mapBoxMap = mapBoxMap
                setDefaultValue(mapBoxMap)
                onMapAsync(mapBoxMap)
            }
        }
    }
}