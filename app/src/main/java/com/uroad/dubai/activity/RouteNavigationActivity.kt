package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener
import com.uroad.dubai.R
import com.uroad.dubai.adapter.DirectionsRouteAdapter
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.adapter.PoiSearchHistoryAdapter
import com.uroad.dubai.api.presenter.*
import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.api.view.PoiSearchView
import com.uroad.dubai.api.view.RouteNavigationView
import com.uroad.dubai.api.view.RouteSubscribeView
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.enumeration.StatisticsType
import com.uroad.dubai.enumeration.SubscribeType
import com.uroad.dubai.local.PoiSearchSource
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.PoiSearchPoiMDL
import com.uroad.dubai.model.PoiSearchTextMDL
import com.uroad.dubai.model.UserAddressMDL
import com.uroad.dubai.rx2bus.MessageEvent
import com.uroad.dubai.rx2bus.Rx2Bus
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.dubai.widget.AppCompatNavigationMapRoute
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.utils.InputMethodUtils
import kotlinx.android.synthetic.main.activity_routenavigation.*
import kotlinx.android.synthetic.main.content_routenavigation.*
import kotlinx.android.synthetic.main.content_routepoisearch.*

/**
 * @author MFB
 * @create 2018/12/22
 * @describe route navigation
 */
class RouteNavigationActivity : BaseNoTitleMapBoxActivity(), RouteNavigationView, PoiSearchView, RouteSubscribeView, UserAddressView {
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var poiKey: String? = ""
    private val poiData = ArrayList<CarmenFeature>()
    private lateinit var poiAdapter: PoiSearchAdapter
    private val historyData = ArrayList<MultiItem>()
    private lateinit var historyAdapter: PoiSearchHistoryAdapter
    private lateinit var poiPresenter: PoiSearchPresenter
    private var poiType = 1
    private var profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var navigationMapRoute: AppCompatNavigationMapRoute? = null
    private var selectedRoute: DirectionsRoute? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var routePresenter: RouteNavigationPresenter
    private var isRouteNavigation = false
    private var isFromUserClick = false
    private var isFromBundle = false
    private var userMarker: Marker? = null
    private var isSimulate = false
    private var planCode: Int = 1
    private lateinit var subscribePresenter: RouteSubscribePresenter
    private var isSubscribe = false
    private var routeId: String? = null
    private var isFromHome = false
    private lateinit var userAddressPresenter: UserAddressPresenter
    private var userAddressType: Int = 1

    private lateinit var statisticsPresenter: FunctionStatisticsPresenter

    override fun setBaseMapBoxView(): Int = R.layout.activity_routenavigation
    override fun onMapSetUp(savedInstanceState: Bundle?) {
        ivBackIM.setOnClickListener { onBackPressed() }
        initPresenter()
        initPoiTextView()
        initBundle()
        initSearch()
        initChangePoi()
        initProfileRg()
        initRoutesRv()
        initBottomView()
    }

    private fun initPresenter() {
        routePresenter = RouteNavigationPresenter(this, this)
        subscribePresenter = RouteSubscribePresenter(this)
        statisticsPresenter = FunctionStatisticsPresenter(this)
    }

    private fun initPoiTextView() {
        tvStartPoint.setOnClickListener {
            poiType = 1
            onShowSearchContent()
        }
        tvEndPoint.setOnClickListener {
            poiType = 2
            onShowSearchContent()
        }
    }

    private fun initBundle() {
        intent.extras?.let { extras ->
            val carmen = extras.getString("destination")
            carmen?.let {
                isFromBundle = true
                val feature = CarmenFeature.fromJson(it)
                endPoint = feature.center()
                tvEndPoint.text = feature.placeName()
            }
            isFromHome = extras.getBoolean("fromHome", false)
            if (isFromHome) {
                val routeId = extras.getString("routeId", "")
                if (!TextUtils.isEmpty(routeId)) {
                    initRoute(routeId)
                }
                val profile = extras.getString("profile", "")
                if (!TextUtils.isEmpty(profile)) {
                    initProfile(profile)
                }
                tvStartPoint.text = extras.getString("startPointName")
                val startPoint = extras.getString("startPoint", "")
                if (!TextUtils.isEmpty(startPoint)) {
                    this.startPoint = Point.fromJson(startPoint)
                }
            }
            val endPoint = extras.getString("endPoint")
            endPoint?.let {
                isFromBundle = true
                this.endPoint = Point.fromJson(it)
                tvEndPoint.text = extras.getString("endPointName")
            }
        }
    }

    private fun initRoute(routeId: String) {
        this.routeId = routeId
        ivSave.setImageResource(R.mipmap.ic_collect_save)
    }

    private fun initProfile(profile: String) {
        this.profile = profile
        when (profile) {
            DirectionsCriteria.PROFILE_DRIVING_TRAFFIC -> {
                radioGroup.check(R.id.rbDrive)
            }
            DirectionsCriteria.PROFILE_CYCLING -> {
                radioGroup.check(R.id.rbBicycle)
            }
            DirectionsCriteria.PROFILE_WALKING -> {
                radioGroup.check(R.id.rbWalk)
            }
            else -> {
                radioGroup.check(R.id.rbDrive)
            }
        }
    }

    private fun initSearch() {
        poiPresenter = PoiSearchPresenter(this, this)
        userAddressPresenter = UserAddressPresenter(this)
        contentSearch.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        ivBack.setOnClickListener { onInitialState() }
        cvSearch.layoutParams = (cvSearch.layoutParams as RelativeLayout.LayoutParams)
        llHome.setOnClickListener {
            if (isLogin()) {
                userAddressType = 1
                userAddressPresenter.getUserAddress(getUserUUID())
            } else {
                openActivity(LoginActivity::class.java)
            }
        }
        llWork.setOnClickListener {
            if (isLogin()) {
                userAddressType = 2
                userAddressPresenter.getUserAddress(getUserUUID())
            } else {
                openActivity(LoginActivity::class.java)
            }
        }
        initEtSearch()
        initRvPoi()
        initHistory()
    }

    /*add search imeOption*/
    private fun initEtSearch() {
        etSearch.inputType = EditorInfo.TYPE_CLASS_TEXT
        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.clearFocus()
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            /*when click search button save content*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = etSearch.text.toString()
                if (!TextUtils.isEmpty(content)) {
                    PoiSearchSource.saveContent(this@RouteNavigationActivity, content)
                    handleWhenInput(content)
                }
            }
            return@setOnEditorActionListener true
        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
                val content = cs.toString()
                if (TextUtils.isEmpty(content)) {
                    whenContentEmpty()
                } else {
                    whenContentFill()
                    handleWhenInput(content)
                }
            }

            override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    /*when etSearch content empty*/
    private fun whenContentEmpty() {
        if (tvSearchTips.visibility != View.VISIBLE) tvSearchTips.visibility = View.VISIBLE
        showHistory()
        removeRunnable()
    }

    /*when etSearch fill content*/
    private fun whenContentFill() {
        if (tvSearchTips.visibility != View.GONE) tvSearchTips.visibility = View.GONE
        if (cvHistory.visibility != View.GONE) cvHistory.visibility = View.GONE
    }

    private fun initRvPoi() {
        poiAdapter = poiPresenter.getPoiSearchAdapter(poiData, object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                if (position in 0 until poiData.size) {
                    PoiSearchSource.saveContent(this@RouteNavigationActivity, poiData[position].toJson())
                    onSelectCarmenFeature(poiData[position])
                }
            }
        })
        rvPoi.adapter = poiAdapter
    }

    /*Initialization Historical Search Records*/
    private fun initHistory() {
        historyAdapter = poiPresenter.getPoiSearchHistoryAdapter(historyData, object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                if (position in 0 until historyData.size) {
                    val item = historyData[position]
                    val itemType = item.getItemType()
                    if (itemType == 1) {
                        val mdl = item as PoiSearchTextMDL
                        etSearch.setText(mdl.content)
                        etSearch.setSelection(etSearch.text.length)
                    } else {
                        val mdl = item as PoiSearchPoiMDL
                        onSelectCarmenFeature(mdl.carmenFeature)
                    }
                }
            }
        })
        rvHistory.adapter = historyAdapter
        showHistory()
    }

    private fun onSelectCarmenFeature(carmenFeature: CarmenFeature?) {
        if (poiType == 1) {
            startPoint = carmenFeature?.center()
            tvStartPoint.text = carmenFeature?.placeName()
        } else {
            endPoint = carmenFeature?.center()
            tvEndPoint.text = carmenFeature?.placeName()
        }
        onInitialState()
        onNavigationRoutes()
    }

    private fun showHistory() {
        val list = PoiSearchSource.getHistoryList(this)
        if (list.size > 0) {
            historyData.clear()
            historyData.addAll(list)
            historyAdapter.notifyDataSetChanged()
            cvHistory.visibility = View.VISIBLE
        } else {
            cvHistory.visibility = View.GONE
        }
        tvClearHistory.setOnClickListener {
            PoiSearchSource.clear(this)
            showHistory()
        }
    }

    private fun onInitialState() {
        contentSearch.visibility = View.GONE
        rvPoi.visibility = View.GONE
        etSearch.text = null
        etSearch.clearFocus()
        InputMethodUtils.hideSoftInput(this, etSearch)
    }

    private fun onShowSearchContent() {
        contentSearch.visibility = View.VISIBLE
        etSearch.requestFocus()
        InputMethodUtils.showSoftInput(this)
    }

    private fun removeRunnable() {
        handler.removeCallbacks(poiSearchRun)
        rvPoi.visibility = View.GONE
    }

    private fun handleWhenInput(content: String) {
        removeRunnable()
        poiKey = content
        handler.postDelayed(poiSearchRun, 500)
    }

    private val poiSearchRun = Runnable {
        poiPresenter.cancelCall()
        poiKey?.let {
            isRouteNavigation = false
            poiPresenter.doPoiSearch(it)
        }
    }


    /*Exchange starting point and end poi*/
    private fun initChangePoi() {
        ivChange.setOnClickListener {
            val temp = startPoint
            startPoint = endPoint
            endPoint = temp
            val tempText = tvStartPoint.text
            tvStartPoint.text = tvEndPoint.text
            tvEndPoint.text = tempText
            onNavigationRoutes()
        }
    }

    private fun initProfileRg() {
        radioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                R.id.rbDrive -> {
                    planCode = 1
                    profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
                }
                R.id.rbBicycle -> {
                    planCode = 2
                    profile = DirectionsCriteria.PROFILE_CYCLING
                }
                R.id.rbWalk -> {
                    planCode = 3
                    profile = DirectionsCriteria.PROFILE_WALKING
                }
            }
            onNavigationRoutes()
        }
    }

    private fun initRoutesRv() {
        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
    }

    private fun initBottomView() {
        ivSave.setOnClickListener { onSaveRoute() }
        ivLocation.setOnClickListener {
            isFromUserClick = true
            openLocation()
        }
        cbSimulate.setOnCheckedChangeListener { _, isChecked -> isSimulate = isChecked }
        tvNavigation.setOnClickListener {
            selectedRoute?.let { route ->
                openActivity(MapNavigationActivity::class.java, Bundle().apply {
                    putString("route", route.toJson())
                    putBoolean("shouldSimulateRoute", isSimulate)
                })
            }
        }
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {
        if (startPoint == null) openLocation()
        onNavigationRoutes()
    }

    override fun onDismissLocationPermission() {
        Snackbar.make(mapView, getString(R.string.location_not_granted), Snackbar.LENGTH_SHORT).show()
    }

    override fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {

    }

    override fun afterLocation(location: Location) {
        initMyLocation(location)
        if (isFromUserClick) {
            moveToUserLocation(location)
        }
    }

    private fun initMyLocation(location: Location) {
        if (startPoint == null) {
            startPoint = Point.fromLngLat(location.longitude, location.latitude)
            tvStartPoint.text = getString(R.string.route_myLocation)
        }
        if (isFromBundle) {
            onNavigationRoutes()
            isFromBundle = false
        }
    }

    private fun moveToUserLocation(location: Location) {
        userMarker?.let { mapBoxMap?.removeMarker(it) }
        val options = MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(IconFactory.getInstance(this).fromResource(R.mipmap.ic_user_location))
        userMarker = mapBoxMap?.addMarker(options)
        var zoom = 17.toDouble()
        mapBoxMap?.cameraPosition?.let { zoom = it.zoom }
        val position = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(zoom).build()
        mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    override fun onPoiResult(features: MutableList<CarmenFeature>) {
        if (features.size > 0) {
            this.poiData.clear()
            this.poiData.addAll(features)
            poiAdapter.notifyDataSetChanged()
            rvPoi.visibility = View.VISIBLE
        }
    }

    override fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?) {
        if (routes == null || routes.isEmpty()) {
            showShortToast(getString(R.string.no_routes_found))
        } else {
            drawRoutes(routes)
        }
    }

    override fun onShowLoading() {
        if (isRouteNavigation || isSubscribe) showLoading()
    }

    override fun onHideLoading() {
        if (isRouteNavigation || isSubscribe) endLoading()
    }

    override fun onShowError(msg: String?) {
        if (isRouteNavigation || isSubscribe) showShortToast(msg)
    }

    private fun onNavigationRoutes() {
        startPoint?.let { startP -> endPoint?.let { endP -> navigationRoutes(startP, endP) } }
    }

    private fun navigationRoutes(origin: Point, destination: Point) {
        InputMethodUtils.hideSoftInput(this)
        llBottom.visibility = View.GONE
        isRouteNavigation = true
        routePresenter.getRoutes(origin, destination, profile)
        if (isFromHome) {
            isFromHome = false
        } else {
            clearSave()
        }
    }

    private fun drawRoutes(routes: List<DirectionsRoute>) {
        originMarker?.let { mapBoxMap?.removeMarker(it) }
        destinationMarker?.let { mapBoxMap?.removeMarker(it) }
        mapBoxMap?.clear()
        mapBoxMap?.let {
            navigationMapRoute?.removeRoute()
            navigationMapRoute = AppCompatNavigationMapRoute(mapView, it).apply { addRoutes(routes) }
        }
        var startPoint: LatLng? = null
        var endPoint: LatLng? = null
        this.startPoint?.let { startPoint = LatLng(it.latitude(), it.longitude()) }
        this.endPoint?.let { endPoint = LatLng(it.latitude(), it.longitude()) }
//        addMarker(startPoint, endPoint)
        zoomToSpan(startPoint, endPoint)
        updateRoutes(routes.toMutableList())
    }

    /*添加起点和终点marker*/
//    private fun addMarker(startPoint: LatLng?, endPoint: LatLng?) {
//        originMarker = mapBoxMap?.addMarker(MarkerOptions().position(startPoint).setIcon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_user_location)))
//        destinationMarker = mapBoxMap?.addMarker(MarkerOptions().position(endPoint).icon(IconFactory.getInstance(this@RouteNavigationActivity).fromResource(R.mipmap.ic_route_target)))
//    }

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
        val adapter = routePresenter.getDirectionsRouteAdapter(this, routes, object : DirectionsRouteAdapter.OnItemSelectedListener {
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
        recyclerView.adapter = adapter
        navigationMapRoute?.setOnRouteSelectionChangeListener(OnRouteSelectionChangeListener {
            for (i in 0 until routes.size) {
                if (TextUtils.equals(it.geometry(), routes[i].geometry()) || it.distance() == routes[i].distance()) {
                    adapter.setSelected(i)
                    break
                }
            }
            selectedRoute = it
        })
    }

    private fun onSaveRoute() {
        isSubscribe = true
        if (!TextUtils.isEmpty(routeId)) {
            subscribePresenter.unSubscribeRoute(getAndroidID(), routeId, SubscribeType.ROUTE.CODE)
        } else {
            val route = selectedRoute ?: return
            subscribePresenter.subscribeRoute(getRouteParams(route))
        }
    }

    private fun getRouteParams(route: DirectionsRoute): HashMap<String, String?> {
        val userId = getAndroidID()
        val startPoint = tvStartPoint.text.toString()
        val endPoint = tvEndPoint.text.toString()
        var startLngLat = ""
        this.startPoint?.let { startLngLat = "${it.longitude()},${it.latitude()}" }
        var endLngLat = ""
        this.endPoint?.let { endLngLat = "${it.longitude()},${it.latitude()}" }
        var lineString: LineString? = null
        route.geometry()?.let { lineString = LineString.fromPolyline(it, Constants.PRECISION_5) }
        val coordinates = ArrayList<com.uroad.dubai.model.map.LatLng>()
        lineString?.let { for (item in it.coordinates()) coordinates.add(com.uroad.dubai.model.map.LatLng(item.latitude() / 10, item.longitude() / 10)) }
        return WebApi.subscribeRouteParams(userId, startPoint, endPoint, startLngLat, endLngLat, "$planCode", "", GsonUtils.fromObjectToJson(coordinates))
    }

    /*subscribe route success*/
    override fun onSuccess(data: String?, isSubscribe: Boolean) {
        if (isSubscribe) {
            data?.let { initRoute(it) }
            showTipsDialog(getString(R.string.save_route_success))
        } else {
            showTipsDialog(getString(R.string.remove_route_success))
            clearSave()
        }
        Rx2Bus.post(MessageEvent())
    }

    /*subscribe route failure*/
    override fun onFailure(errMsg: String?, errCode: Int?) {
        showShortToast(errMsg)
    }

    override fun onSuccess(data: String?, funType: Int) {
    }

    override fun onGetUserAddress(data: MutableList<UserAddressMDL>) {
        if (data.isEmpty()) {
            if (userAddressType == 1) {
                openActivity(NavigationAddressActivity::class.java, Bundle().apply { putString("type", AddressType.HOME.CODE) })
            } else {
                openActivity(NavigationAddressActivity::class.java, Bundle().apply { putString("type", AddressType.WORK.CODE) })
            }
        } else {
            var point: Point? = null
            var address: String? = null
            if (userAddressType == 1) {
                for (item in data) {
                    if (TextUtils.equals(item.addresstype, "1")) {
                        item.getLatLng()?.let { point = Point.fromLngLat(it.longitude, it.latitude) }
                        address = item.address
                        break
                    }
                }
            } else {
                for (item in data) {
                    if (TextUtils.equals(item.addresstype, "2")) {
                        item.getLatLng()?.let { point = Point.fromLngLat(it.longitude, it.latitude) }
                        address = item.address
                        break
                    }
                }
            }
            point?.let { if (poiType == 1) startPoint = it else endPoint = it }
            if (!TextUtils.isEmpty(address)) tvEndPoint.text = address
            onNavigationRoutes()
        }
    }

    private fun clearSave() {
        routeId = null
        ivSave.setImageResource(R.mipmap.ic_collect_default)
    }

    override fun initData() {
        statisticsPresenter.onFuncStatistics(StatisticsType.NAVIGATION_NAVI.CODE)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && contentSearch.visibility != View.GONE) {
            onInitialState()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun release() {
        InputMethodUtils.hideSoftInput(this)
        poiPresenter.detachView()
        routePresenter.detachView()
        subscribePresenter.detachView()
        userAddressPresenter.detachView()
        statisticsPresenter.detachView()
        handler.removeCallbacksAndMessages(null)
    }
}