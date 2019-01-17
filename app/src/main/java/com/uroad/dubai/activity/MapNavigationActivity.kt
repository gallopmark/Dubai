package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions
import com.mapbox.services.android.navigation.v5.navigation.NavigationConstants
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
import com.uroad.dubai.widget.AppCompatNavigationMapRoute
import kotlinx.android.synthetic.main.activity_mapnavigation.*


/**
 * custom navigation activity
 */
class MapNavigationActivity : BaseNoTitleMapBoxActivity(), LocationEngineListener, NavigationListener, BannerInstructionsListener, InstructionListListener, ProgressChangeListener, SpeechAnnouncementListener {

    private var locationEngine: LocationEngine? = null
    private var navigationMapRoute: AppCompatNavigationMapRoute? = null
    private var route: DirectionsRoute? = null

    override fun requestWindow() {
        setTheme(R.style.CustomInstructionView)
    }

    override fun setBaseMapBoxView(): Int = R.layout.activity_mapnavigation

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        intent.extras?.let { route = DirectionsRoute.fromJson(it.getString("route")) }
        initialize()
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {
    }

    private fun initialize() {
    }

    override fun willVoice(announcement: SpeechAnnouncement?): String {
        return "All announcements will be the same."
    }

    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {

    }

    override fun onInstructionListVisibilityChanged(visible: Boolean) {
    }

    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions? {
        return instructions
    }

    override fun onNavigationFinished() {
    }

    override fun onNavigationRunning() {
    }

    override fun onCancelNavigation() {
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable().apply {
            this.priority = LocationEnginePriority.HIGH_ACCURACY
            this.fastestInterval = 500
            this.interval = 1000
            requestLocationUpdates()
            this.addLocationEngineListener(this@MapNavigationActivity)
        }
    }

    private fun extractConfiguration(options: NavigationViewOptions.Builder) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        options.shouldSimulateRoute(preferences
                .getBoolean(NavigationConstants.NAVIGATION_VIEW_SIMULATE_ROUTE, false))
        options.directionsProfile(preferences
                .getString(NavigationConstants.NAVIGATION_VIEW_ROUTE_PROFILE_KEY, DirectionsCriteria.PROFILE_DRIVING_TRAFFIC))
    }

    override fun onConnected() {
    }

    override fun onLocationChanged(location: Location?) {

    }

    override fun onDestroy() {
        // Ensure proper shutdown of the SpeechPlayer
        // Prevent leaks
        removeLocationEngineListener()
        // MapboxNavigation will shutdown the LocationEngine
        super.onDestroy()
    }

    private fun removeLocationEngineListener() {
        locationEngine?.removeLocationUpdates()
    }
}