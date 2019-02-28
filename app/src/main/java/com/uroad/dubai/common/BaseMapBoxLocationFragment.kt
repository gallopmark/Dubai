package com.uroad.dubai.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntRange
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.uroad.dubai.utils.DubaiUtils
import java.lang.Exception
import java.util.ArrayList

abstract class BaseMapBoxLocationFragment : BaseFragment(), PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    open var locationEngine: LocationEngine? = null
    private var handler: Handler? = null
    private var interval: Long = 0L
    private var isDestroyView = false

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
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            val permissionsToExplain = ArrayList<String>()
            for (permission in permissions) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    permissionsToExplain.add(permission)
                }
            }
            if (permissionsToExplain.isNotEmpty()) onExplanationNeeded(permissionsToExplain)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 999)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999 && grantResults.isNotEmpty()) {
            var granted = false
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) granted = true
            }
            onPermissionResult(granted)
        }
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
        locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        val request = buildEngineRequest()
        locationEngine?.requestLocationUpdates(request, this, Looper.getMainLooper())
        if (!DubaiUtils.isLocationEnabled(context)) onLocationFailure(Exception("Location closed"))
        handler?.postDelayed(locationRun, interval)
    }

    private fun buildEngineRequest(): LocationEngineRequest {
        return LocationEngineRequest
                .Builder(1000).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(500).build()
    }

    override fun onSuccess(result: LocationEngineResult?) {
        if (isDestroyView) return
        result?.lastLocation?.let { afterLocation(it) }
    }

    override fun onFailure(exception: Exception) {
        if (isDestroyView) return
        onLocationFailure(exception)
    }

    open fun afterLocation(location: Location) {

    }

    open fun onLocationFailure(exception: Exception) {}

    override fun onDestroyView() {
        closeLocation()
        super.onDestroyView()
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
        isDestroyView = true
    }
}