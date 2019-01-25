package com.uroad.dubai.activity

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.view.Gravity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
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
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.adapter.PoiSearchHistoryAdapter
import com.uroad.dubai.api.presenter.PoiSearchPresenter
import com.uroad.dubai.api.presenter.RoadNavigationPresenter
import com.uroad.dubai.api.presenter.UserAddressPresenter
import com.uroad.dubai.api.view.PoiSearchView
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.local.PoiSearchSource
import com.uroad.dubai.model.*
import com.uroad.library.utils.InputMethodUtils
import kotlinx.android.synthetic.main.content_routepoisearch.*


/**
 * @author MFB
 * @create 2018/12/18
 * @describe Road navigation
 */
class RoadNavigationActivity : BaseNoTitleMapBoxActivity(), RoadNavigationView, PoiSearchView, UserAddressView {
    private var statusHeight = 0
    private var isMapAsync = false
    private var fillExtrusionLayer: FillExtrusionLayer? = null
    private var userMarker: Marker? = null
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

    companion object {
        private const val TYPE_DEFAULT = "default"
        private const val TYPE_3D = "3d"
        private const val TYPE_SATELLITE = "satellite"
    }

    private var mapType = TYPE_DEFAULT

    override fun setBaseMapBoxView(): Int = R.layout.activity_roadnavigation

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        initSource()
        initView()
        initSearch()
        initLayer()
        initMenu()
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
        ivLocation.setOnClickListener { openLocation() }
        ivRouteArrow.setOnClickListener { openActivity(RouteNavigationActivity::class.java) }
    }

    private fun initSearch() {
        poiPresenter = PoiSearchPresenter(this, this)
        userAddressPresenter = UserAddressPresenter(this)
        handler = Handler()
        ivBack.setOnClickListener { onInitialState() }
        cvSearch.layoutParams = (cvSearch.layoutParams as RelativeLayout.LayoutParams).apply { this.topMargin = topMargin + statusHeight }
        llHome.setOnClickListener {
            userAddressType = 1
            userAddressPresenter.getUserAddress(getTestUserId())
        }
        llWork.setOnClickListener {
            userAddressType = 2
            userAddressPresenter.getUserAddress(getTestUserId())
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

    private fun setDefaultStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_default_url))

    private fun set3DStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_3d_url))

    private fun setSatelliteStyleUrl() = mapBoxMap?.setStyle(getString(R.string.map_satellite_url))

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
            mapBoxMap?.setStyle(Style.SATELLITE)
        }
    }

    /*clear map*/
    private fun clearLayer() {
//        mapBoxMap?.clear()
        fillExtrusionLayer?.let { layer -> mapBoxMap?.style?.removeLayer(layer) }
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
            markerObjMap[marker.id]?.let { `object` -> presenter.onMarkerClick(marker, `object`) }
            return@setOnMarkerClickListener true
        }
        setCheckLayer()
//        enableLocationComponent()
    }

    override fun onDismissLocationPermission() {
        Snackbar.make(ivLocation, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
    }

    override fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {
        Snackbar.make(ivLocation, "user location permission not granted", Snackbar.LENGTH_SHORT).show()
    }

    override fun afterLocation(location: Location) {
        moveToUserLocation(location)
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
        presenter.getMapPointByType(code)
    }

    private fun removePointFromMap(code: String) {
        markerMap[code]?.let { for (marker in it) mapBoxMap?.removeMarker(marker) }
    }

    private fun setCheckLayer() {
        cbTrafficLayer.isChecked = true
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
            } else if(userAddressType == 2 && workItem!=null){
                workItem.getLatLng()?.let { onNavigationRoute(Point.fromLngLat(it.longitude, it.latitude), workItem.address) }
            } else {
                openActivity(NavigationAddressActivity::class.java)
            }
        }
    }

    private fun onNavigationRoute(point: Point, endPointName: String?) {
        openActivity(RouteNavigationActivity::class.java, Bundle().apply {
            putString("point", point.toJson())
            putString("endPointName", endPointName)
        })
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && contentSearch.visibility != View.GONE) {
            onInitialState()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        presenter.detachView()
        poiPresenter.detachView()
        userAddressPresenter.detachView()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}