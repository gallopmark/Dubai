package com.uroad.dubai.common

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.uroad.dubai.R

abstract class BaseNoTitleMapBoxActivity : BaseActivity(), LocationEngineListener, PermissionsListener {
    lateinit var mapView: MapView
    var mapBoxMap: MapboxMap? = null
    private var isUserRequestLocation = false
    private var isOpenLocation = false
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null

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

    open fun setDefaultValue(mapBoxMap: MapboxMap) {
        val position = CameraPosition.Builder()
                .target(DubaiApplication.DEFAULT_LATLNG)
                .zoom(DubaiApplication.DEFAULT_ZOOM)
                .build()
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    open fun onMapAsync(mapBoxMap: MapboxMap) {}

    abstract fun setBaseMapBoxView(): Int

    open fun onMapSetUp(savedInstanceState: Bundle?) {

    }

    open fun openLocation() {
        isUserRequestLocation = true
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            onLocation()
        } else {
            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(this@BaseNoTitleMapBoxActivity) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            onLocation()
        } else {
            onDismissLocationPermission()
        }
    }

    open fun onDismissLocationPermission() {}

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        onExplanationLocationPermission(permissionsToExplain)
    }

    open fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {

    }

    @SuppressLint("MissingPermission")
    private fun onLocation() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable().apply {
            val location = this.lastLocation
            if (location != null) {
                afterLocation(location)
            } else {
                this.priority = LocationEnginePriority.HIGH_ACCURACY
                this.addLocationEngineListener(this@BaseNoTitleMapBoxActivity)
                this.activate()
            }
        }
        isOpenLocation = true
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { afterLocation(it) }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    open fun afterLocation(location: Location) {

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        locationEngine?.let {
            it.addLocationEngineListener(this)
            it.requestLocationUpdates()
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (isUserRequestLocation && PermissionsManager.areLocationPermissionsGranted(this) && !isOpenLocation) {
            onLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.let {
            it.removeLocationUpdates()
            it.removeLocationEngineListener(this)
        }
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
        locationEngine?.deactivate()
        mapView.onDestroy()
        super.onDestroy()
    }
}