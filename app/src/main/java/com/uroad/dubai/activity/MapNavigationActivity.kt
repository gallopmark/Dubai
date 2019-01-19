package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.KeyEvent
import android.view.View
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.RecenterButton
import com.mapbox.services.android.navigation.ui.v5.instruction.InstructionView
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener
import com.mapbox.services.android.navigation.ui.v5.summary.SummaryBottomSheet
import com.mapbox.services.android.navigation.v5.milestone.Milestone
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.RoadNavigationPresenter
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.common.BaseMapBoxLocationActivity
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.model.MapPointItem
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.utils.TimeUtils
import com.uroad.dubai.utils.Utils
import kotlinx.android.synthetic.main.activity_mapnavigation.*
import kotlinx.android.synthetic.main.content_mapnavigation.*
import kotlinx.android.synthetic.main.content_mapnavigationoptions.*


/**
 * custom navigation activity
 */
class MapNavigationActivity : BaseMapBoxLocationActivity(), MilestoneEventListener, OffRouteListener, ProgressChangeListener, InstructionListListener, RoadNavigationView {

    companion object {
        private const val DEFAULT_ZOOM = 12.0
        private const val DEFAULT_TILT = 0.0
    }

    private var route: DirectionsRoute? = null
    private var shouldSimulateRoute = false
    private var instructionView: InstructionView? = null
    private var summaryBottomSheet: SummaryBottomSheet? = null
    private var recenterBtn: RecenterButton? = null
    private lateinit var presenter: RoadNavigationPresenter
    private val markerMap = ArrayMap<String, MutableList<Marker>>()
    private var markerObjMap = ArrayMap<Long, MapPointItem>()

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_mapnavigation, true)
        navigationView.onCreate(savedInstanceState)
        initBundle()
        initView()
        onInitState()
        initialize()
        initNavigationButton()
        initNavigationOptions()
        presenter = RoadNavigationPresenter(this, this)
    }

    private fun initView() {
        instructionView = navigationView.findViewById(R.id.instructionView)
        summaryBottomSheet = navigationView.findViewById(R.id.summaryBottomSheet)
        recenterBtn = navigationView.findViewById(R.id.recenterBtn)
    }

    private fun onInitState() {
        instructionView?.visibility = View.GONE
        summaryBottomSheet?.visibility = View.GONE
        recenterBtn?.visibility = View.GONE
    }

    private fun initBundle() {
        intent.extras?.let {
            route = DirectionsRoute.fromJson(it.getString("route"))
            shouldSimulateRoute = it.getBoolean("shouldSimulateRoute", false)
        }
        route?.let { route ->
            val duration = route.duration()
            if (duration != null) {
                val source = TimeUtils.convertSecond2Min(duration.toInt())
                tvDuration.text = SpannableString(source).apply {
                    val sp14 = resources.getDimensionPixelOffset(R.dimen.font_14)
                    val index = if (source.contains("s")) source.indexOf("s") else source.indexOf("m")
                    setSpan(AbsoluteSizeSpan(sp14, false), index, source.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            val distance = route.distance()
            if (distance != null) {
                tvDistance.text = Utils.convertDistance(distance.toInt())
            }
        }
    }

    private fun initialize() {
        navigationView.initialize {
            route?.let { route -> navigationView.startNavigation(buildOptions(route)) }
            openLocation()
            hideNavigationButton()
            onInitState()
        }
    }

    private fun buildOptions(route: DirectionsRoute): NavigationViewOptions {
        return NavigationViewOptions.builder()
                .directionsRoute(route)
                .shouldSimulateRoute(shouldSimulateRoute)
                .navigationOptions(MapboxNavigationOptions.builder().build())
                .instructionListListener(this)
                .milestoneEventListener(this)
                .progressChangeListener(this)
                .build()
    }

    private fun initNavigationButton() {
        ivLocation.setOnClickListener { openLocation() }
        tvExit.setOnClickListener { finish() }
        ivSearchIM.setOnClickListener { contentOptions.visibility = View.VISIBLE }
    }

    private fun initNavigationOptions() {
        contentOptions.setOnClickListener { hideNavigationOptions() }
        cbParking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presenter.getMapPointByType(MapDataType.PARKING.CODE)
            else removePointFromMap(MapDataType.PARKING.CODE)
        }
        cbGasStation.setOnCheckedChangeListener { _, isChecked ->
            showTipsDialog(getString(R.string.developing))
            cbGasStation.isChecked = false
        }
        cbAttraction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presenter.getScenic()
            else removePointFromMap(MapDataType.SCENIC.CODE)
        }
        cbShopping.setOnCheckedChangeListener { _, isChecked ->
            showTipsDialog(getString(R.string.developing))
            cbShopping.isChecked = false
        }
        ivSearch.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
    }

    private fun hideNavigationOptions() {
        contentOptions.visibility = View.GONE
    }

    private fun hideNavigationButton() {
        navigationView.retrieveSoundButton().hide()
        navigationView.retrieveFeedbackButton().hide()
        navigationView.retrieveRecenterButton().hide()
    }

    override fun onInstructionListVisibilityChanged(visible: Boolean) {
        if (visible) {
            onHideView()
        } else {
            onShowView()
        }
    }

    private fun onHideView() {
        ivSearchIM.visibility = View.INVISIBLE
        ivLocation.visibility = View.INVISIBLE
        llBottom.visibility = View.INVISIBLE
    }

    private fun onShowView() {
        ivSearchIM.visibility = View.VISIBLE
        ivLocation.visibility = View.VISIBLE
        llBottom.visibility = View.VISIBLE
    }

    override fun userOffRoute(location: Location?) {
//        speechPlayer?.onOffRoute()
    }

    override fun onMilestoneEvent(routeProgress: RouteProgress?, instruction: String?, milestone: Milestone?) {
        routeProgress?.let { updateProgress(it) }
        instructionView?.let { if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE }
    }

    private fun updateProgress(progress: RouteProgress) {
        val text = "${Utils.convertDistance(progress.distanceRemaining().toInt())}•${TimeUtils.convertSecond2Min(progress.durationRemaining().toInt())}"
        tvDistance.text = text
    }

    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {
        location?.let { updateLocation(it) }
        instructionView?.let { if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE }
    }

    private fun updateLocation(location: Location) {
        navigationView.retrieveNavigationMapboxMap()?.updateLocation(location)
    }

    override fun afterLocation(location: Location) {
        moveCameraTo(location)
    }

    private fun moveCameraTo(location: Location) {
        val cameraPosition = buildCameraPositionFrom(location, location.bearing.toDouble())
        navigationView.retrieveNavigationMapboxMap()?.retrieveMap()?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun buildCameraPositionFrom(location: Location, bearing: Double): CameraPosition {
        return CameraPosition.Builder()
                .zoom(DEFAULT_ZOOM)
                .target(LatLng(location.latitude, location.longitude))
                .bearing(bearing)
                .tilt(DEFAULT_TILT)
                .build()
    }

    override fun onGetScenic(data: MutableList<ScenicMDL>) {
        showPoint(MapDataType.SCENIC.CODE, ArrayList<MapPointItem>().apply { addAll(data) })
    }

    private fun showPoint(code: String, data: MutableList<MapPointItem>) {
        val mapBoxMap = navigationView.retrieveNavigationMapboxMap()?.retrieveMap()
        mapBoxMap?.let {
            val markers = ArrayList<Marker>()
            for (item in data) {
                val marker = it.addMarker(createMarkerOptions(IconFactory.getInstance(this).fromResource(item.getSmallMarkerIcon()), item.getLatLng()))
                markers.add(marker)
                markerObjMap[marker.id] = item
            }
            markerMap[code] = markers
        }
    }

    private fun createMarkerOptions(icon: Icon, latLng: LatLng): MarkerOptions {
        return MarkerOptions().setIcon(icon).setPosition(latLng)
    }

    private fun removePointFromMap(code: String) {
        val mapBoxMap = navigationView.retrieveNavigationMapboxMap()?.retrieveMap()
        markerMap[code]?.let { for (marker in it) mapBoxMap?.removeMarker(marker) }
    }

    override fun onGetMapPoi(code: String, data: MutableList<MapPointItem>) {
        showPoint(code, data)
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && contentOptions.visibility != View.GONE) {
            hideNavigationOptions()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView.onResume()
    }

    override fun onPause() {
        super.onPause()
        navigationView.onPause()
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        navigationView.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView.onRestoreInstanceState(savedInstanceState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView.onLowMemory()
    }

    override fun onDestroy() {
        navigationView.onDestroy()
        super.onDestroy()
    }

}