package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.PopupWindowCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CarmenFeatureAdapter
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.utils.TimeUtils
import com.uroad.dubai.utils.Utils
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.utils.InputMethodUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_routenavigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private var isFirstSetText = false
    private var isStartSetText = false
    private var startKey: String? = ""
    private var isEndSetText = false
    private var endKey: String? = ""
    private var startGeoClient: MapboxGeocoding? = null
    private var endGeoClient: MapboxGeocoding? = null
    private var popupWindow: PopupWindow? = null
    private var profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var selectedRoute: DirectionsRoute? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun setBaseMapBoxView(): Int = R.layout.activity_routenavigation
    override fun onMapSetUp(savedInstanceState: Bundle?) {
        ivBack.setOnClickListener { onBackPressed() }
        initPoiSearchView()
        initChangePoi()
        initProfileRg()
        initRoutesRv()
        initSave()
        initNavigation()
    }

    private fun initPoiSearchView() {
        etStartPoint.clearFocus()
        etStartPoint.addTextChangedListener(InputWatcher(1))
        etEndPoint.addTextChangedListener(InputWatcher(2))
    }

    private inner class InputWatcher(private val type: Int) : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {

        }

        override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
            val content = cs.toString()
            if (TextUtils.isEmpty(content)) resetWhenTextEmpty(type)
            else handleWhenInput(type, content)
        }

        override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }

    private fun resetWhenTextEmpty(type: Int) {
        when (type) {
            1 -> {
                startPoint = null
                handler.removeCallbacks(startPoiRunnable)
            }
            else -> {
                endPoint = null
                handler.removeCallbacks(endPoiRunnable)
            }
        }
    }

    private fun handleWhenInput(type: Int, content: String?) {
        when (type) {
            1 -> {
                when {
                    isFirstSetText -> isFirstSetText = false
                    isStartSetText -> isStartSetText = false
                    else -> {
                        startPoint = null
                        startKey = content
                        handler.removeCallbacks(startPoiRunnable)
                        handler.postDelayed(startPoiRunnable, 500)
                    }
                }
            }
            else -> {
                when {
                    isEndSetText -> isEndSetText = false
                    else -> {
                        endPoint = null
                        endKey = content
                        handler.removeCallbacks(endPoiRunnable)
                        handler.postDelayed(endPoiRunnable, 500)
                    }
                }
            }
        }
    }

    /*Exchange starting point and end poi*/
    private fun initChangePoi() {
        ivChange.setOnClickListener {
            isStartSetText = true
            isEndSetText = true
            val temp = startPoint
            startPoint = endPoint
            endPoint = temp
            val tempText = etStartPoint.text
            etStartPoint.text = etEndPoint.text
            etEndPoint.text = tempText
            etStartPoint.setSelection(etStartPoint.text.length)
            etEndPoint.setSelection(etEndPoint.text.length)
            startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
        }
    }

    private fun initProfileRg() {
        radioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                R.id.rbDrive -> profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
                R.id.rbBicycle -> profile = DirectionsCriteria.PROFILE_CYCLING
                R.id.rbWalk -> profile = DirectionsCriteria.PROFILE_WALKING
            }
            startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
        }
    }

    private fun initRoutesRv() {
        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
    }

    private fun initSave() {
        tvSave.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
    }

    private fun initNavigation() {
        tvNavigation.setOnClickListener {
            selectedRoute?.let { route ->
                // Create a NavigationLauncherOptions object to package everything together
                val options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .shouldSimulateRoute(true)
                        .build()
                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(this, options)
            }
        }
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
        isFirstSetText = true
        etStartPoint.setText(getString(R.string.route_myLocation))
        etStartPoint.setSelection(etStartPoint.text.length)
        etStartPoint.clearFocus()
    }

    private val startPoiRunnable = Runnable {
        cancelStartSearch()
        startKey?.let { enqueueCall(it, 1) }
    }

    /*cancel start poi search*/
    private fun cancelStartSearch() {
        startGeoClient?.cancelCall()
        popupWindow?.let {
            it.dismiss()
            popupWindow = null
        }
    }

    private val endPoiRunnable = Runnable {
        cancelEndSearch()
        endKey?.let { enqueueCall(it, 2) }
    }

    /*cancel end poi search*/
    private fun cancelEndSearch() {
        endGeoClient?.cancelCall()
        popupWindow?.let {
            it.dismiss()
            popupWindow = null
        }
    }

    /*Asynchronous search Poi*/
    private fun enqueueCall(content: String, type: Int) {
        val client = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapBoxToken))
                .country("AE")
                .query(content).build().apply {
                    this.enqueueCall(object : Callback<GeocodingResponse> {
                        override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                            response.body()?.features()?.let { showPopupWindow(type, it) }
                        }

                        override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {

                        }
                    })
                }
        if (type == 1) {
            startGeoClient = client
        } else {
            endGeoClient = client
        }
    }

    private fun showPopupWindow(type: Int, results: MutableList<CarmenFeature>) {
        val recyclerView = RecyclerView(this).apply {
            setBackgroundColor(ContextCompat.getColor(this@RouteNavigationActivity, R.color.white))
            layoutManager = LinearLayoutManager(this@RouteNavigationActivity).apply { orientation = LinearLayoutManager.VERTICAL }
        }
        val parent = if (type == 1) etStartPoint else etEndPoint
        popupWindow = PopupWindow(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = false
            setBackgroundDrawable(ColorDrawable())
            isOutsideTouchable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val location = IntArray(2)
                parent.getLocationInWindow(location)
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) { // 7.1 版本处理
                    val screenHeight = DisplayUtils.getWindowHeight(this@RouteNavigationActivity)
                    height = screenHeight - location[1] - parent.height
                }
                showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + parent.height)
            } else
                PopupWindowCompat.showAsDropDown(this, parent, 0, 0, Gravity.NO_GRAVITY)
        }
        recyclerView.adapter = CarmenFeatureAdapter(this, results).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (type == 1) {
                        startPoint = results[position].center()
                        isStartSetText = true
                        etStartPoint.setText(results[position].placeName())
                        etStartPoint.setSelection(etStartPoint.text.length)
                    } else {
                        endPoint = results[position].center()
                        isEndSetText = true
                        etEndPoint.setText(results[position].placeName())
                        etEndPoint.setSelection(etEndPoint.text.length)
                    }
                    popupWindow?.dismiss()
                    startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
                }
            })
        }
    }

    private fun navigationRoutes(origin: Point, destination: Point) {
        InputMethodUtils.hideSoftInput(this)
        llBottom.visibility = View.GONE
        val directions = MapboxDirections.builder()
                .profile(profile)
                .origin(origin)
                .destination(destination)
                .accessToken(getString(R.string.mapBoxToken))
                .bannerInstructions(true)
                .voiceInstructions(true)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .alternatives(true)
                .steps(true)
                .build()
        addDisposable(Observable.fromCallable { directions.executeCall() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    endLoading()
                    val routes = it.body()?.routes()
                    if (routes == null || routes.isEmpty()) {
                        showShortToast("No routes found")
                    } else {
                        drawRoutes(routes)
                    }
                }, {
                    endLoading()
                    showShortToast(it.message)
                }, {}, { showLoading() }))
    }

    private fun drawRoutes(routes: List<DirectionsRoute>) {
        originMarker?.let { mapBoxMap?.removeMarker(it) }
        destinationMarker?.let { mapBoxMap?.removeMarker(it) }
        mapBoxMap?.clear()
        mapBoxMap?.let {
            navigationMapRoute?.removeRoute()
            navigationMapRoute = NavigationMapRoute(mapView, it).apply { addRoutes(routes) }
        }
        var startPoint: LatLng? = null
        var endPoint: LatLng? = null
        this.startPoint?.let { startPoint = LatLng(it.latitude(), it.longitude()) }
        this.endPoint?.let { endPoint = LatLng(it.latitude(), it.longitude()) }
        addMarker(startPoint, endPoint)
        zoomToSpan(startPoint, endPoint)
        updateRoutes(routes.toMutableList())
    }

    /*添加起点和终点marker*/
    private fun addMarker(startPoint: LatLng?, endPoint: LatLng?) {
        originMarker = mapBoxMap?.addMarker(MarkerOptions().position(startPoint).setIcon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_user_location)))
        destinationMarker = mapBoxMap?.addMarker(MarkerOptions().position(endPoint).icon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_route_target)))
    }

    /*根据起点和终点缩放地图*/
    private fun zoomToSpan(startPoint: LatLng?, endPoint: LatLng?) {
        val builder = LatLngBounds.Builder()
        startPoint?.let { builder.include(it) }
        endPoint?.let { builder.include(it) }
        val paddingLR = DisplayUtils.dip2px(this, 30f)
        val paddingTop = DisplayUtils.dip2px(this, 150f)
        val paddingBottom = DisplayUtils.dip2px(this, 180f)
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), paddingLR, paddingTop, paddingLR, paddingBottom))
    }

    private fun updateRoutes(routes: MutableList<DirectionsRoute>) {
        llBottom.visibility = View.VISIBLE
        selectedRoute = routes[0]
        val adapter = DirectionsRouteAdapter(this, routes).apply {
            setOnItemSelectedListener(object : DirectionsRouteAdapter.OnItemSelectedListener {
                override fun onItemSelected(t: DirectionsRoute, position: Int) {
                    navigationMapRoute?.removeRoute()
                    val data = ArrayList<DirectionsRoute>()
                    data.add(t)
                    for (i in 0 until routes.size) {
                        if (i != position) data.add(routes[i])
                    }
                    navigationMapRoute?.addRoutes(data)
                    selectedRoute = t
                }
            })
        }
        recyclerView.adapter = adapter
        navigationMapRoute?.setOnRouteSelectionChangeListener {
            for (i in 0 until routes.size) {
                if (TextUtils.equals(it.geometry(), routes[i].geometry()) || it.distance() == routes[i].distance()) {
                    adapter.setSelected(i)
                    break
                }
            }
            selectedRoute = it
        }
    }

    private class DirectionsRouteAdapter(context: Activity, routes: MutableList<DirectionsRoute>)
        : BaseArrayRecyclerAdapter<DirectionsRoute>(context, routes) {
        private val params = if (routes.size > 3) {
            LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
        } else {
            LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / routes.size, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        private var selected: Int = 0
        private val sp14 = context.resources.getDimensionPixelOffset(R.dimen.font_14)
        private var onItemSelectedListener: OnItemSelectedListener? = null

        override fun bindView(viewType: Int): Int = R.layout.item_directionsroute

        override fun onBindHoder(holder: RecyclerHolder, t: DirectionsRoute, position: Int) {
            val duration = t.duration()
            if (duration != null) {
                val source = TimeUtils.convertSecond2Min(duration.toInt())
                val ss = SpannableString(source).apply { setSpan(AbsoluteSizeSpan(sp14, false), source.indexOf("m"), source.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
                holder.setText(R.id.tvDuration, ss)
            } else {
                holder.setText(R.id.tvDuration, "")
            }
            val distance = t.distance()
            if (distance != null) {
                holder.setText(R.id.tvDistance, Utils.convertDistance(distance.toInt()))
            } else {
                holder.setText(R.id.tvDistance, "")
            }
            holder.itemView.layoutParams = params
            holder.itemView.setOnClickListener {
                setSelected(position)
                onItemSelectedListener?.onItemSelected(t, position)
            }
            holder.itemView.isSelected = selected == position
        }

        fun setSelected(position: Int) {
            this.selected = position
            notifyDataSetChanged()
        }

        interface OnItemSelectedListener {
            fun onItemSelected(t: DirectionsRoute, position: Int)
        }

        fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener
        }
    }
}