package com.uroad.dubai.activity

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Gravity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.uroad.dubai.R
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_roadnavigation.*
import kotlinx.android.synthetic.main.content_maplayeroption.*
import kotlinx.android.synthetic.main.content_roadnavigation.*
import android.support.v4.util.ArrayMap
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.adapter.PoiSearchHistoryAdapter
import com.uroad.dubai.api.presenter.*
import com.uroad.dubai.api.view.PoiSearchView
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.common.BaseMapBoxLocationActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.enumeration.StatisticsType
import com.uroad.dubai.local.PoiSearchSource
import com.uroad.dubai.model.*
import com.uroad.dubai.utils.MapController
import com.uroad.dubai.utils.MyMapUtils
import com.uroad.library.utils.InputMethodUtils
import kotlinx.android.synthetic.main.content_mapview.*
import kotlinx.android.synthetic.main.content_routepoisearch.*
import java.lang.Exception


/**
 * @author MFB
 * @create 2018/12/18
 * @describe Road navigation
 */
class RoadNavigationActivity : BaseMapBoxLocationActivity(), RoadNavigationView, PoiSearchView, UserAddressView, MapboxMap.OnMapClickListener {
    private var statusHeight = 0
    private var isMapAsync = false
    private var mapBoxMap: MapboxMap? = null
    private var fillExtrusionLayer: FillExtrusionLayer? = null
    private val markerMap = ArrayMap<String, MutableList<Marker>>()
    private var markerObjMap = ArrayMap<Long, MapPointItem>()
    private lateinit var presenter: RoadNavigationPresenter
    private lateinit var poiPresenter: PoiSearchPresenter
    private lateinit var handler: Handler
    private var poiKey: String? = null
    private val poiData = ArrayList<CarmenFeature>()
    private lateinit var poiAdapter: PoiSearchAdapter
    private val historyData = ArrayList<MultiItem>()
    private lateinit var historyAdapter: PoiSearchHistoryAdapter
    private lateinit var userAddressPresenter: UserAddressPresenter
    private var userAddressType: Int = 1

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private lateinit var statisticsPresenter: FunctionStatisticsPresenter

    private var isUserLocation = false
    private var isDrawBundleMarker = false
    private var myLocation: Location? = null
    private lateinit var mapController: MapController

    companion object {
        private const val TYPE_DEFAULT = "default"
        private const val TYPE_3D = "3d"
        private const val TYPE_SATELLITE = "satellite"
    }

    private var mapType = TYPE_DEFAULT

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_roadnavigation)
        mapView.onCreate(savedInstanceState)
        mapController = MapController(this)
        getMapAsync()
        initSource()
        initView()
        initSearch()
        initLayer()
        initMenu()
        statisticsPresenter = FunctionStatisticsPresenter(this)
    }

    private fun getMapAsync() {
        mapView.getMapAsync {
            isMapAsync = true
            this.mapBoxMap = it
            setDefaultValue(it)
            it.setOnMarkerClickListener { marker ->
                markerObjMap[marker.id]?.let { `object` -> presenter.onMarkerClick(marker, `object`) }
                return@setOnMarkerClickListener true
            }
            it.addOnMapClickListener(this)
            isUserLocation = false
        }
    }

    private fun setDefaultValue(mapBoxMap: MapboxMap) {
        val position = CameraPosition.Builder()
                .target(DubaiApplication.DEFAULT_LATLNG)
                .zoom(DubaiApplication.DEFAULT_ZOOM)
                .build()
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        cbTrafficLayer.isChecked = true
        mapBoxMap.setStyle(getString(R.string.map_default_url)) { openLocation() }
    }

    override fun onMapClick(point: LatLng): Boolean {
        val pointF = mapBoxMap?.projection?.toScreenLocation(point) ?: return false
        val features = mapBoxMap?.queryRenderedFeatures(pointF) ?: return false
        if (features.isNotEmpty()) {
            var pointProperty: String? = null
            for (feature in features) {
                val property = feature.properties()?.get("pointProperty")?.asString
                if (property != null && mapController.pointMap()[property] != null) {
                    pointProperty = property
                    break
                }
            }
            mapController.pointMap()[pointProperty]?.let { presenter.onMapClick(it) }
        }
        return true
    }

    private fun initSource() {
        statusHeight = DisplayUtils.getStatusHeight(this)
        presenter = RoadNavigationPresenter(this, this)
    }

    private fun initView() {
        cvSearchIM.setOnClickListener { onShowSearchContent() }
        ivBackIM.setOnClickListener { onBackPressed() }
        cvSearchIM.layoutParams = (cvSearchIM.layoutParams as ConstraintLayout.LayoutParams).apply { this.topMargin = topMargin + statusHeight }
        setDrawerEdgeSize()
        ivSwitchLayer.setOnClickListener {
            if (drawerLayout.isDrawerOpen(Gravity.END)) {
                drawerLayout.closeDrawer(Gravity.END)
            } else {
                drawerLayout.openDrawer(Gravity.END)
            }
        }
        cbTrafficLayer.setOnCheckedChangeListener { _, isChecked -> changeTrafficLayer(isChecked) }
        ivTouristLayer.setOnClickListener { onScenic() }
        ivReportLayer.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivEnlarge.setOnClickListener { enlargeMap() }
        ivNarrow.setOnClickListener { narrowMap() }
        ivLocation.setOnClickListener {
            isUserLocation = true
            openLocation()
        }
        ivRouteArrow.setOnClickListener { openActivity(RouteNavigationActivity::class.java) }
    }

    private fun initSearch() {
        poiPresenter = PoiSearchPresenter(this, this)
        userAddressPresenter = UserAddressPresenter(this)
        handler = Handler()
        ivBack.setOnClickListener { onInitialState() }
        cvSearch.layoutParams = (cvSearch.layoutParams as RelativeLayout.LayoutParams).apply { this.topMargin = topMargin + statusHeight }
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
                    PoiSearchSource.saveContent(this@RoadNavigationActivity, content)
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
                    PoiSearchSource.saveContent(this@RoadNavigationActivity, poiData[position].toJson())
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
        openActivity(RouteNavigationActivity::class.java, Bundle().apply { putString("destination", carmenFeature?.toJson()) })
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
        cvSearchIM.visibility = View.VISIBLE
        rvPoi.visibility = View.GONE
        etSearch.text = null
        etSearch.clearFocus()
        InputMethodUtils.hideSoftInput(this, etSearch)
    }

    private fun onShowSearchContent() {
        cvSearchIM.visibility = View.INVISIBLE
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
        poiKey?.let { poiPresenter.doPoiSearch(it) }
    }

    override fun onPoiResult(features: MutableList<CarmenFeature>) {
        if (features.size > 0) {
            this.poiData.clear()
            this.poiData.addAll(features)
            poiAdapter.notifyDataSetChanged()
            rvPoi.visibility = View.VISIBLE
        }
    }

    private fun moveToUserLocation(location: Location) {
        MyMapUtils.clearMyLocationMarker(mapBoxMap?.style)
        MyMapUtils.drawMyLocationMarker(mapBoxMap?.style, location, ContextCompat.getDrawable(this, R.mipmap.ic_user_location))
        MyMapUtils.animateCamera(mapBoxMap, LatLng(location.latitude, location.longitude), 17.toDouble())
    }

    private fun changeTrafficLayer(isChecked: Boolean) {
        if (isChecked) {
            when (mapType) {
                TYPE_DEFAULT -> {
                    if (!isMapAsync) return
                    clearLayer()
                    setDefaultStyleUrl()
                }
                TYPE_3D -> {
                    if (!isMapAsync) return
                    clearLayer()
                    set3DStyleUrl()
                }
                TYPE_SATELLITE -> {
                    if (!isMapAsync) return
                    clearLayer()
                    setSatelliteStyleUrl()
                }
            }
        } else {
            when (mapType) {
                TYPE_DEFAULT -> setDefaultMapType()
                TYPE_3D -> set3DMapType()
                TYPE_SATELLITE -> setSatelliteMapType()
            }
        }
    }

    private fun setDefaultStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_default_url)) { whenStyleLoaded() }

    private fun set3DStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_3d_url)) { whenStyleLoaded() }

    private fun setSatelliteStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_satellite_url)) { whenStyleLoaded() }

    //侧滑菜单占屏幕的7/10
    private fun setDrawerEdgeSize() {
        val params = layerOption.layoutParams
        params.width = (DisplayUtils.getWindowWidth(this) * 2 / 3)
        layerOption.layoutParams = params
    }

    private fun onScenic() {
        removePointFromMap(MapDataType.SCENIC.CODE)
        presenter.getScenic()
        clearChecks()
    }

    private fun initLayer() {
        llDefault.isSelected = true
        llDefault.setOnClickListener { if (!llDefault.isSelected) setSelected(1) }
        ll3DMap.setOnClickListener { if (!ll3DMap.isSelected) setSelected(2) }
        llSatellite.setOnClickListener { if (!llSatellite.isSelected) setSelected(3) }
    }

    private fun setSelected(layer: Int) {
        llDefault.isSelected = false
        ll3DMap.isSelected = false
        llSatellite.isSelected = false
        when (layer) {
            1 -> {
                llDefault.isSelected = true
                setDefaultMapType()
            }
            2 -> {
                ll3DMap.isSelected = true
                set3DMapType()
            }
            3 -> {
                llSatellite.isSelected = true
                setSatelliteMapType()
            }
        }
    }

    /*default map*/
    private fun setDefaultMapType() {
        if (!isMapAsync) return
        mapType = TYPE_DEFAULT
        clearLayer()
        if (cbTrafficLayer.isChecked) {
            setDefaultStyleUrl()
        } else {
            mapBoxMap?.setStyle(Style.MAPBOX_STREETS) { whenStyleLoaded() }
        }
    }

    /*3d map*/
    private fun set3DMapType() {
        if (!isMapAsync) return
        mapType = TYPE_3D
        clearLayer()
        if (cbTrafficLayer.isChecked) {
            set3DStyleUrl()
        } else {
            mapBoxMap?.setStyle(Style.LIGHT) {
                whenStyleLoaded()
                fill3DLayer()
            }
        }
    }

    private fun fill3DLayer() {
        fillExtrusionLayer?.let { layer -> mapBoxMap?.style?.removeLayer(layer) }
        fillExtrusionLayer = presenter.get3DBuildingLayer().apply { mapBoxMap?.style?.addLayer(this) }
    }

    /*satellite map*/
    private fun setSatelliteMapType() {
        if (!isMapAsync) return
        mapType = TYPE_SATELLITE
        clearLayer()
        if (cbTrafficLayer.isChecked) {
            setSatelliteStyleUrl()
        } else {
            mapBoxMap?.setStyle(Style.SATELLITE) { whenStyleLoaded() }
        }
    }

    private fun whenStyleLoaded() {
        if (isUserLocation) myLocation?.let { moveToUserLocation(it) }
        for ((k, v) in mapController.dataMap()) {
            showPoint(k, v)
        }
    }

    /*clear map*/
    private fun clearLayer() {
//        mapBoxMap?.clear()
        mapController.removeAllWhenStyleChanged(mapBoxMap?.style)
        fillExtrusionLayer?.let { layer -> mapBoxMap?.style?.removeLayer(layer) }
        fillExtrusionLayer = null
    }

    /*enlarge map*/
    private fun enlargeMap() {
        mapController.enlargeMap(mapBoxMap)
    }

    /*narrow map*/
    private fun narrowMap() {
        mapController.narrowMap(mapBoxMap)
    }

    private fun initMenu() {
        val onCheckChangedListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
            when (view.id) {
                R.id.cbEvents -> onMenuCheckChanged(isChecked, MapDataType.ACCIDENT.CODE)
                R.id.cbParking -> onMenuCheckChanged(isChecked, MapDataType.PARKING.CODE)
                R.id.cbCCTV -> onMenuCheckChanged(isChecked, MapDataType.CCTV.CODE)
                R.id.cbDMS -> onMenuCheckChanged(isChecked, MapDataType.DMS.CODE)
                R.id.cbPolice -> onMenuCheckChanged(isChecked, MapDataType.POLICE.CODE)
                R.id.cbWeather -> onMenuCheckChanged(isChecked, MapDataType.WEATHER.CODE)
                R.id.cbRWIS -> onMenuCheckChanged(isChecked, MapDataType.RWIS.CODE)
                R.id.cbBusStop -> onMenuCheckChanged(isChecked, MapDataType.BUS_STOP.CODE)
            }
        }
        cbEvents.setOnCheckedChangeListener(onCheckChangedListener)
        cbParking.setOnCheckedChangeListener(onCheckChangedListener)
        cbCCTV.setOnCheckedChangeListener(onCheckChangedListener)
        cbDMS.setOnCheckedChangeListener(onCheckChangedListener)
        cbPolice.setOnCheckedChangeListener(onCheckChangedListener)
        cbWeather.setOnCheckedChangeListener(onCheckChangedListener)
        cbRWIS.setOnCheckedChangeListener(onCheckChangedListener)
        cbBusStop.setOnCheckedChangeListener(onCheckChangedListener)
    }

    private fun onMenuCheckChanged(isChecked: Boolean, code: String) {
        if (isChecked) {
            getMapPointByType(code)
        } else {
            removePointFromMap(code)
        }
    }

    private fun removePointFromMap(code: String) {
        val style = mapBoxMap?.style
        mapController.removePoint(style, code)
        markerMap[code]?.let { for (marker in it) mapBoxMap?.removeMarker(marker) }
    }

    override fun onDismissLocationPermission() {
        Snackbar.make(ivLocation, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
    }

    override fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {
        Snackbar.make(ivLocation, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
    }

    override fun afterLocation(location: Location) {
        myLocation = location
        longitude = location.longitude
        latitude = location.latitude
        onMapDataSource()
        if (isUserLocation) {
            moveToUserLocation(location)
        }
        closeLocation()
    }

    override fun onLocationFailure(exception: Exception) {
        onMapDataSource()
    }

    private fun onMapDataSource() {
        if (isDrawBundleMarker) return
        val mdl = intent.extras?.getSerializable("dataMDL")
        if (mdl != null) {
            drawMarker(mdl as NewsMDL)
        } else {
            setCheckLayer()
        }
        isDrawBundleMarker = true
    }

    private fun drawMarker(mdl: NewsMDL) {
        val sourceId = "bundle-point-source-id"
        val feature = Feature.fromGeometry(Point.fromLngLat(mdl.getLatLng().longitude, mdl.getLatLng().latitude)).apply { addStringProperty("pointProperty", "bundle-point-property") }
        val layerId = "bundle-point-layer-id"
        val imageName = "bundle-point-image-id"
        MyMapUtils.addSinglePoint(mapBoxMap?.style, sourceId, feature, layerId, imageName, ContextCompat.getDrawable(this, mdl.getSmallMarkerIcon()))
        mapController.pointMap()["bundle-point-property"] = mdl
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLng(mdl.getLatLng()))
//        val marker = mapBoxMap?.addMarker(createMarkerOptions(IconFactory.getInstance(this).fromResource(mdl.getSmallMarkerIcon()), mdl.getLatLng()))
//        marker?.let { markerObjMap[it.id] = mdl }
//        marker?.let { presenter.onMarkerClick(it, mdl) }
    }

    private fun setCheckLayer() {
        cbEvents.isChecked = true
        cbParking.isChecked = true
        cbCCTV.isChecked = true
        cbDMS.isChecked = true
        cbPolice.isChecked = true
        cbWeather.isChecked = true
        cbRWIS.isChecked = true
        cbBusStop.isChecked = true
    }

    private fun clearChecks() {
        cbEvents.isChecked = false
        cbParking.isChecked = false
        cbCCTV.isChecked = false
        cbDMS.isChecked = false
        cbPolice.isChecked = false
        cbWeather.isChecked = false
        cbRWIS.isChecked = false
        cbBusStop.isChecked = false
    }

    private fun getMapPointByType(type: String) {
        presenter.getMapPointByType(type, longitude, latitude)
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {}

    override fun onShowError(msg: String?) {}


    override fun onGetScenic(data: MutableList<ScenicMDL>) {
        showPoint(MapDataType.SCENIC.CODE, ArrayList<MapPointItem>().apply { addAll(data) })
    }

    override fun onGetMapPoi(code: String, data: MutableList<MapPointItem>) {
        if (code == MapDataType.WEATHER.CODE) {
            showWeather(code, data)
        } else {
            showPoint(code, data)
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }

    private fun showPoint(code: String, data: MutableList<MapPointItem>) {
        mapController.showPoint(mapBoxMap?.style, code, data)
    }

    private fun showWeather(code: String, items: MutableList<MapPointItem>) {
        val markers = ArrayList<Marker>()
        for (item in items) {
            val marker = mapBoxMap?.addMarker(createMarkerOptions(IconFactory.getInstance(this).fromBitmap(generate(item as WeatherMDL)), item.getLatLng()))
            marker?.let { markers.add(it) }
        }
        markerMap[code] = markers
    }

    private fun generate(item: WeatherMDL): Bitmap {
        return presenter.generateWeather(item)
    }

    private fun createMarkerOptions(icon: Icon, latLng: LatLng): MarkerOptions {
        return MarkerOptions().setIcon(icon).setPosition(latLng)
    }

    override fun onSuccess(data: String?, funType: Int) {
    }

    override fun onGetUserAddress(data: MutableList<UserAddressMDL>) {
        if (data.isEmpty()) {
            if (userAddressType == 1) {
                openActivity(NavigationAddressActivity::class.java)
            } else {
                openActivity(NavigationAddressActivity::class.java)
            }
        } else {
            var homeItem: UserAddressMDL? = null
            var workItem: UserAddressMDL? = null
            for (item in data) {
                if (TextUtils.equals(item.addresstype, AddressType.HOME.CODE) && userAddressType == 1) {
                    homeItem = item
                    break
                } else if (TextUtils.equals(item.addresstype, AddressType.WORK.CODE) && userAddressType == 2) {
                    workItem = item
                    break
                }
            }
            if (userAddressType == 1 && homeItem != null) {
                homeItem.getLatLng()?.let { onNavigationRoute(Point.fromLngLat(it.longitude, it.latitude), homeItem.address) }
            } else if (userAddressType == 2 && workItem != null) {
                workItem.getLatLng()?.let { onNavigationRoute(Point.fromLngLat(it.longitude, it.latitude), workItem.address) }
            } else {
                openActivity(NavigationAddressActivity::class.java)
            }
        }
    }

    private fun onNavigationRoute(point: Point, endPointName: String?) {
        openActivity(RouteNavigationActivity::class.java, Bundle().apply {
            putString("endPoint", point.toJson())
            putString("endPointName", endPointName)
        })
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
    }

    override fun initData() {
        statisticsPresenter.onFuncStatistics(StatisticsType.NAVIGATION_SEARCH.CODE)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && contentSearch.visibility != View.GONE) {
            onInitialState()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let { mapView.onSaveInstanceState(it) }
    }

    override fun onDestroy() {
        release()
        mapView.onDestroy()
        super.onDestroy()
    }

    private fun release() {
        presenter.detachView()
        poiPresenter.detachView()
        userAddressPresenter.detachView()
        statisticsPresenter.detachView()
        handler.removeCallbacksAndMessages(null)
    }
}