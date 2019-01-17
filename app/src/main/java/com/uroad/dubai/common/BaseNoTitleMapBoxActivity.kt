package com.uroad.dubai.common

import android.os.Bundle
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.permissions.PermissionsListener
import com.uroad.dubai.R

abstract class BaseNoTitleMapBoxActivity : BaseMapBoxActivity(), LocationEngineListener, PermissionsListener {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(setBaseMapBoxView(), true)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        onMapSetUp(savedInstanceState)
        mapView.getMapAsync {
            mapBoxMap = it
            setDefaultValue(it)
            onMapAsync(it)
        }
    }
}