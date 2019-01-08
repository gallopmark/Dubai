package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.view.Gravity
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseNoTitleMapBoxActivity
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_roadnavigation.*
import kotlinx.android.synthetic.main.content_maplayeroption.*
import kotlinx.android.synthetic.main.content_roadnavigation.*
import android.support.v4.util.ArrayMap
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.adapter.PoiSearchHistoryAdapter
import com.uroad.dubai.api.presenter.RoadNavigationPresenter
import com.uroad.dubai.api.presenter.RouteNavigationPresenter
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.api.view.RouteNavigationView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.dialog.*
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.local.DataSource
import com.uroad.dubai.local.PoiSearchSource
import com.uroad.dubai.model.*
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.utils.SymbolGenerator
import com.uroad.library.utils.InputMethodUtils
import kotlinx.android.synthetic.main.content_routepoisearch.*


/**
 * @author MFB
 * @create 2018/12/18
 * @describe Road navigation
 */
class RoadNavigationActivity : BaseNoTitleMapBoxActivity(), PermissionsListener, LocationEngineListener, RoadNavigationView, RouteNavigationView {
    private var statusHeight = 0
    private var isMapAsync = false
    private var fillExtrusionLayer: FillExtrusionLayer? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null
    private var userMarker: Marker? = null
    private val markerMap = ArrayMap<String, MutableList<Marker>>()
    private var markerObjMap = ArrayMap<Long, MapPointItem>()
    private lateinit var presenter: RoadNavigationPresenter
    private lateinit var routePresenter: RouteNavigationPresenter
    private lateinit var handler: Handler
    private var poiKey: String? = null
    private val poiData = ArrayList<CarmenFeature>()
    private lateinit var poiAdapter: PoiSearchAdapter
    private val historyData = ArrayList<MultiItem>()
    private lateinit var histotyAdapter: PoiSearchHistoryAdapter

    companion object {
        private const val TYPE_DEFAULT = "default"
        private const val TYPE_3D = "3d"
        private const val TYPE_SATELLITE = "satellite"
    }

    private var mapType = TYPE_DEFAULT

    override fun setBaseMapBoxView(): Int = R.layout.activity_roadnavigation

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        statusHeight = DisplayUtils.getStatusHeight(this)
        initView()
        initSearch()
        initLayer()
        initMenu()
        presenter = RoadNavigationPresenter(this)
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
        cbTouristLayer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presenter.getScenic()
            else removePointFromMap(MapDataType.SCENIC.CODE)
        }
        ivReportLayer.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivEnlarge.setOnClickListener { enlargeMap() }
        ivNarrow.setOnClickListener { narrowMap() }
        ivLocation.setOnClickListener { enableLocationComponent() }
    }

    private fun initSearch() {
        routePresenter = RouteNavigationPresenter(this, this)
        handler = Handler()
        ivBack.setOnClickListener { onInitialState() }
        cvSearch.layoutParams = (cvSearch.layoutParams as RelativeLayout.LayoutParams).apply { this.topMargin = topMargin + statusHeight }
        initEtSearch()
        initRvPoi()
        initHistory()
    }

    private fun initEtSearch() {
        etSearch.inputType = EditorInfo.TYPE_CLASS_TEXT
        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.clearFocus()
        etSearch.setOnEditorActionListener { _, actionId, _ ->
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

    private fun whenContentEmpty() {
        if (tvSearchTips.visibility != View.VISIBLE) tvSearchTips.visibility = View.VISIBLE
        showHistory()
        removeRunnable()
    }

    private fun whenContentFill() {
        if (tvSearchTips.visibility != View.GONE) tvSearchTips.visibility = View.GONE
        if (cvHistory.visibility != View.GONE) cvHistory.visibility = View.GONE
    }

    private fun initRvPoi() {
        poiAdapter = PoiSearchAdapter(this, poiData).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until poiData.size) {
                        PoiSearchSource.saveContent(this@RoadNavigationActivity, GsonUtils.fromObjectToJson(poiData[position]))
                    }
                }
            })
        }
        rvPoi.adapter = poiAdapter
    }

    private fun initHistory() {
        histotyAdapter = PoiSearchHistoryAdapter(this, historyData).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until historyData.size) {
                        val item = historyData[position]
                        val itemType = item.getItemType()
                        if (itemType == 1) {
                            val mdl = item as PoiSearchTextMDL
                            mdl.content?.let { handleWhenInput(it) }
                        } else {
                            val mdl = item as PoiSearchPoiMDL
                        }
                    }
                }
            })
        }
        rvHistory.adapter = histotyAdapter
        showHistory()
    }

    private fun showHistory() {
        val list = PoiSearchSource.getHistoryList(this)
        if (list.size > 0) {
            historyData.clear()
            historyData.addAll(list)
            histotyAdapter.notifyDataSetChanged()
            cvHistory.visibility = View.VISIBLE
        } else {
            cvHistory.visibility = View.GONE
        }
    }

    private fun onInitialState() {
        contentSearch.visibility = View.GONE
        cvSearchIM.visibility = View.VISIBLE
        etSearch.clearFocus()
    }

    private fun onShowSearchContent() {
        cvSearchIM.visibility = View.INVISIBLE
        contentSearch.visibility = View.VISIBLE
        etSearch.requestFocus()
        InputMethodUtils.showSoftInput(this, etSearch)
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
        routePresenter.cancelPoiCall()
        poiKey?.let { routePresenter.getPoi(it) }
    }

    override fun onPoiResult(features: MutableList<CarmenFeature>, type: Int) {
        if (features.size > 0) {
            this.poiData.clear()
            this.poiData.addAll(features)
            poiAdapter.notifyDataSetChanged()
            rvPoi.visibility = View.VISIBLE
        }
    }

    override fun onNavigationRoutes(routes: MutableList<DirectionsRoute>?) {
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

    private fun setDefaultStyleUrl() = mapBoxMap?.setStyleUrl(getString(R.string.map_default_url))

    private fun set3DStyleUrl() = mapBoxMap?.setStyleUrl(getString(R.string.map_3d_url))

    private fun setSatelliteStyleUrl() = mapBoxMap?.setStyleUrl(getString(R.string.map_satellite_url))

    //侧滑菜单占屏幕的7/10
    private fun setDrawerEdgeSize() {
        val params = layerOption.layoutParams
        params.width = (DisplayUtils.getWindowWidth(this) * 2 / 3)
        layerOption.layoutParams = params
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
            mapBoxMap?.setStyle(Style.MAPBOX_STREETS)
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
            mapBoxMap?.setStyle(Style.LIGHT) { fill3DLayer() }
        }
    }

    private fun fill3DLayer() {
        fillExtrusionLayer?.let { layer -> mapBoxMap?.removeLayer(layer) }
        fillExtrusionLayer = FillExtrusionLayer("3d-buildings", "composite").apply {
            sourceLayer = "building"
            filter = Expression.eq(Expression.get("extrude"), "true")
            minZoom = 15f
            setProperties(PropertyFactory.fillExtrusionColor(Color.LTGRAY),
                    PropertyFactory.fillExtrusionHeight(Expression.interpolate(Expression.exponential(1f),
                            Expression.zoom(),
                            Expression.stop(15, Expression.literal(0)),
                            Expression.stop(16, Expression.get("height")))),
                    PropertyFactory.fillExtrusionBase(Expression.get("min_height")),
                    PropertyFactory.fillExtrusionOpacity(0.9f)
            )
            mapBoxMap?.addLayer(this)
        }
    }

    /*satellite map*/
    private fun setSatelliteMapType() {
        if (!isMapAsync) return
        mapType = TYPE_SATELLITE
        clearLayer()
        if (cbTrafficLayer.isChecked) {
            setSatelliteStyleUrl()
        } else {
            mapBoxMap?.setStyle(Style.SATELLITE)
        }
    }

    /*clear map*/
    private fun clearLayer() {
//        mapBoxMap?.clear()
        fillExtrusionLayer?.let { layer -> mapBoxMap?.removeLayer(layer) }
        fillExtrusionLayer = null
    }

    /*enlarge map*/
    private fun enlargeMap() {
        mapBoxMap?.let {
            it.cameraPosition.apply {
                var mapZoom = zoom
                scaleMap(target, ++mapZoom)
            }
        }
    }

    /*narrow map*/
    private fun narrowMap() {
        mapBoxMap?.let {
            it.cameraPosition.apply {
                var mapZoom = zoom
                scaleMap(target, --mapZoom)
            }
        }
    }

    /*map enlarge or narrow*/
    private fun scaleMap(nowLocation: LatLng, scaleValue: Double) {
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, scaleValue))
    }

    /*map load complete*/
    override fun onMapAsync(mapBoxMap: MapboxMap) {
        isMapAsync = true
        mapBoxMap.setOnMarkerClickListener { marker ->
            markerObjMap[marker.id]?.let { `object` -> onMarkerClick(marker, `object`) }
            return@setOnMarkerClickListener true
        }
        setCheckLayer()
//        enableLocationComponent()
    }

    /*marker onClick*/
    private fun onMarkerClick(marker: Marker, item: MapPointItem) {
        var dialog: Dialog? = null
        marker.icon = IconFactory.getInstance(this).fromResource(item.getBigMarkerIcon())
        when (item) {
            is EventsMDL -> dialog = EventsDetailDialog(this, item)
            is ParkingMDL -> {
                dialog = ParkingDetailDialog(this, item).setOnNavigateListener(object : ParkingDetailDialog.OnNavigateListener {
                    override fun onNavigate(mdl: ParkingMDL, dialog: ParkingDetailDialog) {

                    }
                })
            }
            is CCTVSnapMDL -> dialog = CCTVSnapDetailDialog(this, item)
            is DMSysMDL -> dialog = DMSDetailDialog(this, item)
            is PoliceMDL -> {
                dialog = PoliceDetailDialog(this, item).setOnNavigateListener(object : PoliceDetailDialog.OnNavigateListener {
                    override fun onNavigate(mdl: PoliceMDL, dialog: PoliceDetailDialog) {

                    }
                })
            }
            is RWISMDL -> dialog = RWISDetailDialog(this, item)
            is BusStopMDL -> {
                dialog = BusStopDetailDialog(this, item).setOnNavigateListener(object : BusStopDetailDialog.OnNavigateListener {
                    override fun onNavigate(mdl: BusStopMDL, dialog: BusStopDetailDialog) {

                    }
                })
            }
        }
        dialog?.show()
        dialog?.setOnDismissListener { marker.icon = IconFactory.getInstance(this).fromResource(item.getSmallMarkerIcon()) }
    }

    /*location*/
    private fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            onLocation()
        } else {
            permissionsManager = PermissionsManager(this).apply { requestLocationPermissions(this@RoadNavigationActivity) }
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
            Snackbar.make(ivLocation, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocation() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable().apply {
            val location = this.lastLocation
            if (location != null) {
                moveToUserLocation(location)
            } else {
                this.priority = LocationEnginePriority.HIGH_ACCURACY
                this.addLocationEngineListener(this@RoadNavigationActivity)
                this.activate()
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { moveToUserLocation(it) }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
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
            showMapPointByType(code)
        } else {
            removePointFromMap(code)
        }
    }

    private fun showMapPointByType(code: String) {
        when (code) {
            MapDataType.ACCIDENT.CODE -> {
                val data = ArrayList<MapPointItem>().apply {
                    addAll(DataSource.MapData.getAccident())
                    addAll(DataSource.MapData.getConstruction())
                }
                showPoint(code, data)
            }
            MapDataType.CONSTRUCTION.CODE -> {
            }
            MapDataType.PARKING.CODE -> showPoint(code, DataSource.MapData.getParking())
            MapDataType.CCTV.CODE -> showPoint(code, DataSource.MapData.getCCTV())
            MapDataType.DMS.CODE -> showPoint(code, DataSource.MapData.getDMS())
            MapDataType.POLICE.CODE -> showPoint(code, DataSource.MapData.getPolice())
            MapDataType.WEATHER.CODE -> showWeather(code, DataSource.MapData.getWeather())
            MapDataType.RWIS.CODE -> showPoint(code, DataSource.MapData.getRWIS())
            MapDataType.BUS_STOP.CODE -> showPoint(code, DataSource.MapData.getBusStop())
        }
    }

    private fun showPoint(code: String, data: MutableList<MapPointItem>) {
        val markers = ArrayList<Marker>()
        for (item in data) {
            val marker = mapBoxMap?.addMarker(createMarkerOptions(IconFactory.getInstance(this).fromResource(item.getSmallMarkerIcon()), item.getLatLng()))
            marker?.let {
                markerObjMap[it.id] = item
                markers.add(it)
            }
        }
        markerMap[code] = markers
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
        val view = LayoutInflater.from(this).inflate(R.layout.content_map_weather, LinearLayout(this), false)
        val tvCity = view.findViewById<TextView>(R.id.tvCity)
        val tvTemperature = view.findViewById<TextView>(R.id.tvTemperature)
        val ivWeather = view.findViewById<ImageView>(R.id.ivWeather)
        tvCity.text = item.city
        tvTemperature.text = item.temperature
        ivWeather.setImageResource(R.mipmap.ic_weather_sun)
        return SymbolGenerator.generate(view)
    }

    private fun createMarkerOptions(icon: Icon, latLng: LatLng): MarkerOptions {
        return MarkerOptions().setIcon(icon).setPosition(latLng)
    }

    private fun removePointFromMap(code: String) {
        markerMap[code]?.let { for (marker in it) mapBoxMap?.removeMarker(marker) }
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

    override fun onShowLoading() {
    }

    override fun onHideLoading() {}

    override fun onShowError(msg: String?) {}

    override fun onGetScenic(data: MutableList<ScenicMDL>) {
        showPoint(MapDataType.SCENIC.CODE, ArrayList<MapPointItem>().apply { addAll(data) })
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && contentSearch.visibility != View.GONE) {
            onInitialState()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        locationEngine?.let {
            it.addLocationEngineListener(this)
            it.requestLocationUpdates()
        }
        super.onStart()
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        locationEngine?.let {
            it.removeLocationUpdates()
            it.removeLocationEngineListener(this)
        }
        super.onStop()
    }

    override fun onDestroy() {
        locationEngine?.deactivate()
        presenter.detachView()
        routePresenter.detachView()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}