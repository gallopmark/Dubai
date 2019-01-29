//package com.uroad.dubai.activity
//
//import android.app.Activity
//import android.location.Location
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.support.design.widget.Snackbar
//import android.support.v7.widget.LinearLayoutManager
//import android.text.Editable
//import android.text.SpannableString
//import android.text.TextUtils
//import android.text.TextWatcher
//import android.text.style.AbsoluteSizeSpan
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.PopupWindow
//import com.mapbox.api.directions.v5.DirectionsCriteria
//import com.mapbox.api.directions.v5.models.DirectionsRoute
//import com.mapbox.api.geocoding.v5.MapboxGeocoding
//import com.mapbox.api.geocoding.v5.models.CarmenFeature
//import com.mapbox.geojson.Point
//import com.mapbox.mapboxsdk.annotations.Marker
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
//import com.mapbox.mapboxsdk.geometry.LatLng
//import com.mapbox.mapboxsdk.geometry.LatLngBounds
//import com.mapbox.mapboxsdk.maps.MapboxMap
//import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
//import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
//import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener
//import com.uroad.dubai.R
//import com.uroad.dubai.common.BaseArrayRecyclerAdapter
//import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
//import com.uroad.dubai.common.BaseRecyclerAdapter
//import com.uroad.dubai.utils.TimeUtils
//import com.uroad.dubai.utils.DubaiUtils
//import com.uroad.dubai.api.presenter.RouteNavigationPresenter
//import com.uroad.dubai.api.view.RouteNavigationView
//import com.uroad.dubai.widget.AppCompatNavigationMapRoute
//import com.uroad.library.utils.DisplayUtils
//import com.uroad.library.utils.InputMethodUtils
//import kotlinx.android.synthetic.main.activity_routenavigation.*
//
///**
// * @author MFB
// * @create 2018/12/22
// * @describe route navigation
// */
//class RouteNavigationV7Activity : BaseNoTitleMapBoxActivity(), RouteNavigationView {
//
//    private var startPoint: Point? = null
//    private var endPoint: Point? = null
//    private var isFirstSetText = false
//    private var isStartSetText = false
//    private var startKey: String? = ""
//    private var isEndSetText = false
//    private var endKey: String? = ""
//    private var startGeoClient: MapboxGeocoding? = null
//    private var endGeoClient: MapboxGeocoding? = null
//    private var popupWindow: PopupWindow? = null
//    private var profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
////    private var originMarker: Marker? = null
////    private var destinationMarker: Marker? = null
//    private var navigationMapRoute: AppCompatNavigationMapRoute? = null
//    private var selectedRoute: DirectionsRoute? = null
//    private val handler = Handler(Looper.getMainLooper())
//    private lateinit var routePresenter: RouteNavigationPresenter
//    private var isRouteNavigation = false
//    private var inputType = 1
//
//    override fun setBaseMapBoxView(): Int = R.layout.activity_routenavigation
//    override fun onMapSetUp(savedInstanceState: Bundle?) {
//        ivBack.setOnClickListener { onBackPressed() }
//        routePresenter = RouteNavigationPresenter(this, this)
//        initPoiSearchView()
//        initChangePoi()
//        initProfileRg()
//        initRoutesRv()
//        initSave()
//        initNavigation()
//    }
//
//    private fun initPoiSearchView() {
//        etStartPoint.clearFocus()
//        etStartPoint.addTextChangedListener(InputWatcher(1))
//        etEndPoint.addTextChangedListener(InputWatcher(2))
//        val carmen = intent.extras?.getString("destination")
//        carmen?.let {
//            val feature = CarmenFeature.fromJson(it)
//            endPoint = feature.center()
//            isEndSetText = true
//            etEndPoint.setText(feature.placeName())
//            etEndPoint.setSelection(etEndPoint.text.length)
//            startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
//        }
//    }
//
//    private inner class InputWatcher(private val userstatus: Int) : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//
//        }
//
//        override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
//            val content = cs.toString()
//            if (TextUtils.isEmpty(content)) resetWhenTextEmpty(userstatus)
//            else handleWhenInput(userstatus, content)
//        }
//
//        override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        }
//    }
//
//    private fun resetWhenTextEmpty(userstatus: Int) {
//        when (userstatus) {
//            1 -> {
//                startPoint = null
//                handler.removeCallbacks(startPoiRunnable)
//            }
//            else -> {
//                endPoint = null
//                handler.removeCallbacks(endPoiRunnable)
//            }
//        }
//    }
//
//    private fun handleWhenInput(userstatus: Int, content: String?) {
//        when (userstatus) {
//            1 -> {
//                when {
//                    isFirstSetText -> isFirstSetText = false
//                    isStartSetText -> isStartSetText = false
//                    else -> {
//                        startPoint = null
//                        startKey = content
//                        handler.removeCallbacks(startPoiRunnable)
//                        handler.postDelayed(startPoiRunnable, 500)
//                    }
//                }
//            }
//            else -> {
//                when {
//                    isEndSetText -> isEndSetText = false
//                    else -> {
//                        endPoint = null
//                        endKey = content
//                        handler.removeCallbacks(endPoiRunnable)
//                        handler.postDelayed(endPoiRunnable, 500)
//                    }
//                }
//            }
//        }
//    }
//
//    /*Exchange starting point and end poi*/
//    private fun initChangePoi() {
//        ivChange.setOnClickListener {
//            isStartSetText = true
//            isEndSetText = true
//            val temp = startPoint
//            startPoint = endPoint
//            endPoint = temp
//            val tempText = etStartPoint.text
//            etStartPoint.text = etEndPoint.text
//            etEndPoint.text = tempText
//            etStartPoint.setSelection(etStartPoint.text.length)
//            etEndPoint.setSelection(etEndPoint.text.length)
//            startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
//        }
//    }
//
//    private fun initProfileRg() {
//        radioGroup.setOnCheckedChangeListener { _, checkId ->
//            when (checkId) {
//                R.id.rbDrive -> profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
//                R.id.rbBicycle -> profile = DirectionsCriteria.PROFILE_CYCLING
//                R.id.rbWalk -> profile = DirectionsCriteria.PROFILE_WALKING
//            }
//            startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
//        }
//    }
//
//    private fun initRoutesRv() {
//        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
//    }
//
//    private fun initSave() {
//        tvSave.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
//    }
//
//    private fun initNavigation() {
//        tvNavigation.setOnClickListener {
//            selectedRoute?.let { route ->
//                // Create a NavigationLauncherOptions object to package everything together
//                val options = NavigationLauncherOptions.builder()
//                        .directionsRoute(route)
//                        .shouldSimulateRoute(true)
//                        .build()
//                // Call this method with Context from within an Activity
//                NavigationLauncher.startNavigation(this, options)
//            }
//        }
//    }
//
//    override fun onMapAsync(mapBoxMap: MapboxMap) {
//        openLocation()
//    }
//
//    override fun onDismissLocationPermission() {
//        Snackbar.make(mapView, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
//    }
//
//    override fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {
//
//    }
//
//    override fun afterLocation(location: Location) {
//        moveToUserLocation(location)
//    }
//
//    private fun moveToUserLocation(location: Location) {
//        startPoint = Point.fromLngLat(location.longitude, location.latitude)
//        isFirstSetText = true
//        etStartPoint.setText(getString(R.string.route_myLocation))
//        etStartPoint.setSelection(etStartPoint.text.length)
//        etStartPoint.clearFocus()
//        startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
//    }
//
//    private val startPoiRunnable = Runnable {
//        cancelStartSearch()
//        startKey?.let { enqueueCall(it, 1) }
//    }
//
//    /*cancel start poi search*/
//    private fun cancelStartSearch() {
//        startGeoClient?.cancelCall()
//        popupWindow?.let {
//            it.dismiss()
//            popupWindow = null
//        }
//    }
//
//    private val endPoiRunnable = Runnable {
//        cancelEndSearch()
//        endKey?.let { enqueueCall(it, 2) }
//    }
//
//    /*cancel end poi search*/
//    private fun cancelEndSearch() {
//        endGeoClient?.cancelCall()
//        popupWindow?.let {
//            it.dismiss()
//            popupWindow = null
//        }
//    }
//
//    override fun onPoiResult(features: MutableList<CarmenFeature>) {
//        showPopupWindow(features)
//    }
//
//    override fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?) {
//        if (routes == null || routes.isEmpty()) {
//            showShortToast("No routes found")
//        } else {
//            drawRoutes(routes)
//        }
//    }
//
//    override fun onShowLoading() {
//        if (isRouteNavigation) showLoading()
//    }
//
//    override fun onHideLoading() {
//        if (isRouteNavigation) endLoading()
//    }
//
//    override fun onShowError(msg: String?) {
//        if (isRouteNavigation) showShortToast(msg)
//    }
//
//    /*Asynchronous search Poi*/
//    private fun enqueueCall(content: String, userstatus: Int) {
//        isRouteNavigation = false
//        inputType = userstatus
//        val client = routePresenter.doPoiSearch(content)
//        if (userstatus == 1) {
//            startGeoClient = client
//        } else {
//            endGeoClient = client
//        }
//    }
//
//    private fun showPopupWindow(results: MutableList<CarmenFeature>) {
//        val parent = if (inputType == 1) etStartPoint else etEndPoint
//        popupWindow = routePresenter.showPoiWindow(parent, results, object : BaseRecyclerAdapter.OnItemClickListener {
//            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
//                if (inputType == 1) {
//                    startPoint = results[position].center()
//                    isStartSetText = true
//                    etStartPoint.setText(results[position].placeName())
//                    etStartPoint.setSelection(etStartPoint.text.length)
//                } else {
//                    endPoint = results[position].center()
//                    isEndSetText = true
//                    etEndPoint.setText(results[position].placeName())
//                    etEndPoint.setSelection(etEndPoint.text.length)
//                }
//                popupWindow?.dismiss()
//                startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
//            }
//        })
//    }
//
//    private fun navigationRoutes(origin: Point, destination: Point) {
//        InputMethodUtils.hideSoftInput(this)
//        llBottom.visibility = View.GONE
//        isRouteNavigation = true
//        routePresenter.getRoutes(origin, destination, profile)
//    }
//
//    private fun drawRoutes(routes: List<DirectionsRoute>) {
////        originMarker?.let { mapBoxMap?.removeMarker(it) }
////        destinationMarker?.let { mapBoxMap?.removeMarker(it) }
////        mapBoxMap?.clear()
//        mapBoxMap?.let {
//            navigationMapRoute?.removeRoute()
//            navigationMapRoute = AppCompatNavigationMapRoute(mapView, it).apply { addRoutes(routes) }
//        }
//        var startPoint: LatLng? = null
//        var endPoint: LatLng? = null
//        this.startPoint?.let { startPoint = LatLng(it.latitude(), it.longitude()) }
//        this.endPoint?.let { endPoint = LatLng(it.latitude(), it.longitude()) }
////        addMarker(startPoint, endPoint)
//        zoomToSpan(startPoint, endPoint)
//        updateRoutes(routes.toMutableList())
//    }
//
//    /*添加起点和终点marker*/
////    private fun addMarker(startPoint: LatLng?, endPoint: LatLng?) {
////        originMarker = mapBoxMap?.addMarker(MarkerOptions().position(startPoint).setIcon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_user_location)))
////        destinationMarker = mapBoxMap?.addMarker(MarkerOptions().position(endPoint).icon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_route_target)))
////    }
//
//    /*根据起点和终点缩放地图*/
//    private fun zoomToSpan(startPoint: LatLng?, endPoint: LatLng?) {
//        val builder = LatLngBounds.Builder()
//        startPoint?.let { builder.include(it) }
//        endPoint?.let { builder.include(it) }
//        val paddingLR = DisplayUtils.dip2px(this, 30f)
//        val paddingTop = DisplayUtils.dip2px(this, 150f)
//        val paddingBottom = DisplayUtils.dip2px(this, 180f)
//        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), paddingLR, paddingTop, paddingLR, paddingBottom))
//    }
//
//    private fun updateRoutes(routes: MutableList<DirectionsRoute>) {
//        llBottom.visibility = View.VISIBLE
//        selectedRoute = routes[0]
//        val adapter = DirectionsRouteAdapter(this, routes).apply {
//            setOnItemSelectedListener(object : DirectionsRouteAdapter.OnItemSelectedListener {
//                override fun onItemSelected(t: DirectionsRoute, position: Int) {
//                    navigationMapRoute?.removeRoute()
//                    val data = ArrayList<DirectionsRoute>()
//                    data.add(t)
//                    for (i in 0 until routes.size) {
//                        if (i != position) data.add(routes[i])
//                    }
//                    navigationMapRoute?.addRoutes(data)
//                    selectedRoute = t
//                }
//            })
//        }
//        recyclerView.adapter = adapter
//        navigationMapRoute?.setOnRouteSelectionChangeListener(OnRouteSelectionChangeListener {
//            for (i in 0 until routes.size) {
//                if (TextUtils.equals(it.geometry(), routes[i].geometry()) || it.distance() == routes[i].distance()) {
//                    adapter.setSelected(i)
//                    break
//                }
//            }
//            selectedRoute = it
//        })
//    }
//
//    private class DirectionsRouteAdapter(context: Activity, routes: MutableList<DirectionsRoute>)
//        : BaseArrayRecyclerAdapter<DirectionsRoute>(context, routes) {
//        private val params = if (routes.size > 3) {
//            LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
//        } else {
//            LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / routes.size, LinearLayout.LayoutParams.WRAP_CONTENT)
//        }
//        private var selected: Int = 0
//        private val sp14 = context.resources.getDimensionPixelOffset(R.dimen.font_14)
//        private var onItemSelectedListener: OnItemSelectedListener? = null
//
//        override fun bindView(viewType: Int): Int = R.layout.item_directionsroute
//
//        override fun onBindHoder(holder: RecyclerHolder, t: DirectionsRoute, position: Int) {
//            val duration = t.duration()
//            if (duration != null) {
//                val source = TimeUtils.convertSecond2Min(duration.toInt())
//                val ss = SpannableString(source).apply { setSpan(AbsoluteSizeSpan(sp14, false), source.indexOf("m"), source.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
//                holder.setText(R.id.tvDuration, ss)
//            } else {
//                holder.setText(R.id.tvDuration, "")
//            }
//            val distance = t.distance()
//            if (distance != null) {
//                holder.setText(R.id.tvDistance, DubaiUtils.convertDistance(distance.toInt()))
//            } else {
//                holder.setText(R.id.tvDistance, "")
//            }
//            holder.itemView.layoutParams = params
//            holder.itemView.setOnClickListener {
//                setSelected(position)
//                onItemSelectedListener?.onItemSelected(t, position)
//            }
//            holder.itemView.isSelected = selected == position
//        }
//
//        fun setSelected(position: Int) {
//            this.selected = position
//            notifyDataSetChanged()
//        }
//
//        interface OnItemSelectedListener {
//            fun onItemSelected(t: DirectionsRoute, position: Int)
//        }
//
//        fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {
//            this.onItemSelectedListener = onItemSelectedListener
//        }
//    }
//
//    override fun onDestroy() {
//        InputMethodUtils.hideSoftInput(this)
//        cancelStartSearch()
//        cancelEndSearch()
//        routePresenter.detachView()
//        super.onDestroy()
//    }
//}