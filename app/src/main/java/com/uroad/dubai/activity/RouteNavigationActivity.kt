package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
import kotlinx.android.synthetic.main.activity_routenavigation.*

/**
 * @author MFB
 * @create 2018/12/22
 * @describe route navigation
 */
class RouteNavigationActivity : BaseNoTitleMapBoxActivity(), PermissionsListener {

    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    override fun setBaseMapBoxView(): Int = R.layout.activity_routenavigation
    override fun onMapSetUp(savedInstanceState: Bundle?) {
        initPoiSearchView()
    }

    private fun initPoiSearchView() {
        etStartPoint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {

            }
            override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {
        enableLocationComponent()
    }

    private fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            onLocation()
        } else {
            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(this@RouteNavigationActivity) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            onLocation()
        } else {
            Snackbar.make(mapView, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocation() {
        val options = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(false)
                .enableStaleState(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.green))
                .build()
        // Get an instance of the component
        locationComponent = mapBoxMap?.locationComponent
        locationComponent?.let {
            it.activateLocationComponent(this, options)
            it.cameraMode = CameraMode.TRACKING
            it.renderMode = RenderMode.COMPASS
            it.lastKnownLocation?.let { location -> moveToUserLocation(location) }
            it.isLocationComponentEnabled = false
        }
    }

    private fun moveToUserLocation(location: Location) {
        startPoint = Point.fromLngLat(location.longitude, location.latitude)
        etStartPoint.setText(getString(R.string.route_myLocation))
    }
}