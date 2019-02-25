package com.uroad.dubai.common

import android.annotation.SuppressLint
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntRange
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.uroad.dubai.utils.DubaiUtils
import java.lang.Exception

abstract class BaseMapBoxLocationFragment : BaseFragment(), PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    private var isUserRequestLocation = false
    private var isOpenLocation = false
    private var permissionsManager: PermissionsManager? = null
    open var locationEngine: LocationEngine? = null
    private var locationEngineRequest: LocationEngineRequest? = null
    private var handler: Handler? = null
    private var interval: Long = 0L

    open fun openLocation() {
        onLocationGranted()
    }

    open fun openLocation(@IntRange(from = 1000, to = 10 * 1000) interval: Long) {
        if (interval > 0) {
            this.interval = interval
            handler = Handler(Looper.getMainLooper())
        }
        onLocationGranted()
    }

    private fun onLocationGranted() {
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
        locationEngine = LocationEngineProvider.getBestLocationEngine(context).apply { locationEngineRequest = buildEngineRequest().apply { requestLocationUpdates(this, this@BaseMapBoxLocationFragment, null) } }
        if (!DubaiUtils.isLocationEnabled(context)) onLocationFailure(Exception("Location closed"))
        handler?.postDelayed({ onLocation() }, interval)
    }

    private fun buildEngineRequest(): LocationEngineRequest {
        return LocationEngineRequest
                .Builder(1000).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(500).build()
    }

    override fun onSuccess(result: LocationEngineResult?) {
        isOpenLocation = true
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
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        if (isUserRequestLocation && PermissionsManager.areLocationPermissionsGranted(context) && !isOpenLocation) {
            onLocation()
        }
    }

    override fun onDestroy() {
        closeLocation()
        super.onDestroy()
    }
}