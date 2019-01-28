package com.uroad.dubai.common

import android.annotation.SuppressLint
import android.location.Location
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.tencent.bugly.Bugly.applicationContext
import java.lang.Exception

abstract class BaseMapBoxLocationFragment : BaseFragment(), PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    private var isUserRequestLocation = false
    private var isOpenLocation = false
    private var permissionsManager: PermissionsManager? = null
    var locationEngine: LocationEngine? = null
    private var locationEngineRequest: LocationEngineRequest? = null

    open fun openLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            onLocation()
        } else {
            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(context) }
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
        locationEngine = LocationEngineProvider.getBestLocationEngine(context).apply {
            locationEngineRequest = buildEngineRequest().apply { requestLocationUpdates(this, this@BaseMapBoxLocationFragment, null) }
        }
        isOpenLocation = true
    }

    private fun buildEngineRequest(): LocationEngineRequest {
        return LocationEngineRequest
                .Builder(1000).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(500).build()
    }

    override fun onSuccess(result: LocationEngineResult?) {
        result?.lastLocation?.let { afterLocation(it) }
    }

    override fun onFailure(exception: Exception) {
        onLocationFailure(exception)
    }

    open fun afterLocation(location: Location) {

    }

    open fun onLocationFailure(exception: Exception) {}

    open fun closeLocation() {
        locationEngine?.removeLocationUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        if (isUserRequestLocation && PermissionsManager.areLocationPermissionsGranted(context) && !isOpenLocation) {
            onLocation()
        }
    }

    override fun onDestroy() {
        locationEngine?.removeLocationUpdates(this)
        super.onDestroy()
    }
}