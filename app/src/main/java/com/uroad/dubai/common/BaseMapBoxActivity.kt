package com.uroad.dubai.common

import android.annotation.SuppressLint
import android.os.Bundle
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.R

abstract class BaseMapBoxActivity : BaseMapBoxLocationActivity() {
    lateinit var mapView: MapView
    var mapBoxMap: MapboxMap? = null
    var mapStyle: Style? = null

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(setBaseMapBoxView())
        initMapView(savedInstanceState)
    }

    fun initMapView(savedInstanceState: Bundle?) {
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        onMapSetUp(savedInstanceState)
        mapView.getMapAsync { mapBoxMap ->
            mapBoxMap.setStyle(Style.MAPBOX_STREETS) {
                this@BaseMapBoxActivity.mapBoxMap = mapBoxMap
                this@BaseMapBoxActivity.mapStyle = it
                setDefaultValue(mapBoxMap)
                onMapAsync(mapBoxMap, it)
            }
        }
    }

    open fun setDefaultValue(mapBoxMap: MapboxMap) {
        val position = CameraPosition.Builder()
                .target(DubaiApplication.DEFAULT_LATLNG)
                .zoom(DubaiApplication.DEFAULT_ZOOM)
                .build()
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    open fun onMapAsync(mapBoxMap: MapboxMap, style: Style){}

    abstract fun setBaseMapBoxView(): Int

    open fun onMapSetUp(savedInstanceState: Bundle?) {

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let { mapView.onSaveInstanceState(it) }
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
}