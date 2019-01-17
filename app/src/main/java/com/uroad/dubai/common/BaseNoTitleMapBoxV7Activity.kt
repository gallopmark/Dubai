//package com.uroad.dubai.common
//
//import android.annotation.SuppressLint
//import android.location.Location
//import android.os.Bundle
//import com.mapbox.android.core.location.*
//import com.mapbox.android.core.permissions.PermissionsListener
//import com.mapbox.android.core.permissions.PermissionsManager
//import com.mapbox.mapboxsdk.camera.CameraPosition
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
//import com.mapbox.mapboxsdk.maps.MapView
//import com.mapbox.mapboxsdk.maps.MapboxMap
//import com.mapbox.mapboxsdk.maps.Style
//import com.uroad.dubai.R
//import java.lang.Exception
//
//abstract class BaseNoTitleMapBoxV7Activity : BaseActivity(), PermissionsListener, LocationEngineCallback<LocationEngineResult> {
//    lateinit var mapView: MapView
//    var mapBoxMap: MapboxMap? = null
//    private var isUserRequestLocation = false
//    private var isOpenLocation = false
//    private var permissionsManager: PermissionsManager? = null
//    private var locationEngine: LocationEngine? = null
//    private var locationEngineRequest: LocationEngineRequest? = null
//
//    override fun setUp(savedInstanceState: Bundle?) {
//        setBaseContentViewWithoutTitle(setBaseMapBoxView(), true)
//        mapView = findViewById(R.id.mapView)
//        mapView.onCreate(savedInstanceState)
//        onMapSetUp(savedInstanceState)
//        mapView.getMapAsync {
//            it.setStyle(Style.MAPBOX_STREETS)
//            mapBoxMap = it
//            setDefaultValue(it)
//            onMapAsync(it)
//        }
//    }
//
//    open fun setDefaultValue(mapBoxMap: MapboxMap) {
//        val position = CameraPosition.Builder()
//                .target(DubaiApplication.DEFAULT_LATLNG)
//                .zoom(DubaiApplication.DEFAULT_ZOOM)
//                .build()
//        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
//    }
//
//    open fun onMapAsync(mapBoxMap: MapboxMap) {}
//
//    abstract fun setBaseMapBoxView(): Int
//
//    open fun onMapSetUp(savedInstanceState: Bundle?) {
//
//    }
//
//    open fun openLocation() {
//        isUserRequestLocation = true
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            onLocation()
//        } else {
//            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(this@BaseNoTitleMapBoxV7Activity) }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onPermissionResult(granted: Boolean) {
//        if (granted) {
//            onLocation()
//        } else {
//            onDismissLocationPermission()
//        }
//    }
//
//    open fun onDismissLocationPermission() {}
//
//    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
//        onExplanationLocationPermission(permissionsToExplain)
//    }
//
//    open fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun onLocation() {
//        locationEngine = LocationEngineProvider.getBestLocationEngine(applicationContext).apply {
//            locationEngineRequest = buildEngineRequest().apply {
//                requestLocationUpdates(this, this@BaseNoTitleMapBoxV7Activity, null)
//            }
//        }
////        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable().apply {
////            val location = this.lastLocation
////            if (location != null) {
////                afterLocation(location)
////            } else {
////                this.priority = LocationEnginePriority.HIGH_ACCURACY
////                this.addLocationEngineListener(this@BaseMapBoxActivity)
////                this.activate()
////            }
////        }
//        isOpenLocation = true
//    }
//
//    private fun buildEngineRequest(): LocationEngineRequest {
//        return LocationEngineRequest
//                .Builder(1000).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
//                .setFastestInterval(500).build()
//    }
//
//    override fun onSuccess(result: LocationEngineResult?) {
//        result?.lastLocation?.let { afterLocation(it) }
//    }
//
//    override fun onFailure(exception: Exception) {
//    }
//
//    open fun afterLocation(location: Location) {
//
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onStart() {
//        super.onStart()
//        locationEngineRequest?.let { locationEngine?.requestLocationUpdates(it, this, null) }
//        mapView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//        if (isUserRequestLocation && PermissionsManager.areLocationPermissionsGranted(this) && !isOpenLocation) {
//            onLocation()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        outState?.let { mapView.onSaveInstanceState(it) }
//    }
//
//    override fun onDestroy() {
//        locationEngine?.removeLocationUpdates(this)
//        mapView.onDestroy()
//        super.onDestroy()
//    }
//}