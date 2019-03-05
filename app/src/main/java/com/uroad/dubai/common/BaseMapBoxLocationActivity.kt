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

abstract class BaseMapBoxLocationActivity : BaseActivity(), PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    private var permissionsManager: PermissionsManager? = null
    open var locationEngine: LocationEngine? = null
    private var handler: Handler? = null
    private var interval: Long = 0L
    private var isLocationClosed = false

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
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            onLocation()
        } else {
            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(this@BaseMapBoxLocationActivity) }
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
        if (locationEngine != null) releaseLocation()
        isLocationClosed = false
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val request = buildEngineRequest()
        locationEngine?.requestLocationUpdates(request, this, Looper.getMainLooper())
        if (!DubaiUtils.isLocationEnabled(this)) onLocationFailure(Exception("Location closed"))
        handler?.postDelayed(locationRun, interval)
    }

    private fun buildEngineRequest(): LocationEngineRequest {
        return LocationEngineRequest
                .Builder(interval).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(500).build()
    }

    override fun onSuccess(result: LocationEngineResult?) {
        if (isFinishing || isLocationClosed) return
        result?.lastLocation?.let { afterLocation(it) }
    }

    override fun onFailure(exception: Exception) {
        if (isFinishing || isLocationClosed) return
        onLocationFailure(exception)
    }

    open fun afterLocation(location: Location) {

    }

    open fun onLocationFailure(exception: Exception) {}

    override fun onDestroy() {
        closeLocation()
        super.onDestroy()
    }

    private val locationRun = Runnable { onLocation() }

    open fun releaseLocation() {
        closeLocation()
    }

    open fun closeLocation() {
        locationEngine?.let {
            it.removeLocationUpdates(this)
            locationEngine = null
        }
        handler?.removeCallbacks(locationRun)
        isLocationClosed = true
    }
}