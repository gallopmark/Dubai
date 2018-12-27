package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.location.Location
import android.os.Bundle
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
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.location.modes.CameraMode
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.widget.CompoundButton
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.uroad.dubai.dialog.*
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.local.DataSource
import com.uroad.dubai.model.*


/**
 * @author MFB
 * @create 2018/12/18
 * @describe Road navigation
 */
class RoadNavigationActivity : BaseNoTitleMapBoxActivity(), PermissionsListener {

    private var statusHeight = 0
    private var isMapAsync = false
    private var fillExtrusionLayer: FillExtrusionLayer? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var userMarker: Marker? = null
    private val markerMap = ArrayMap<String, MutableList<Marker>>()
    private var markerObjMap = ArrayMap<Long, MapPointItem>()

    override fun setBaseMapBoxView(): Int = R.layout.activity_roadnavigation

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        statusHeight = DisplayUtils.getStatusHeight(this)
        initView()
        initLayer()
        initMenu()
    }

    private fun initView() {
        rlSearch.setOnClickListener { openActivity(RouteNavigationActivity::class.java) }
        ivBack.setOnClickListener { onBackPressed() }
        rlSearch.layoutParams = (rlSearch.layoutParams as ConstraintLayout.LayoutParams).apply { this.topMargin = topMargin + statusHeight }
        ivSwitchLayer.setOnClickListener {
            if (drawerLayout.isDrawerOpen(Gravity.END)) {
                drawerLayout.closeDrawer(Gravity.END)
            } else {
                drawerLayout.openDrawer(Gravity.END)
            }
        }
        setDrawerEdgeSize()
        ivEnlarge.setOnClickListener { enlargeMap() }
        ivNarrow.setOnClickListener { narrowMap() }
        ivLocation.setOnClickListener { enableLocationComponent() }
    }

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
        clearLayer()
        mapBoxMap?.setStyle(Style.LIGHT)
    }

    /*3d map*/
    private fun set3DMapType() {
        if (!isMapAsync) return
        clearLayer()
        mapBoxMap?.setStyle(Style.LIGHT) {
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
    }

    /*satellite map*/
    private fun setSatelliteMapType() {
        if (!isMapAsync) return
        clearLayer()
        mapBoxMap?.setStyle(Style.SATELLITE)
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
            MapDataType.WEATHER.CODE -> {

            }
            MapDataType.RWIS.CODE -> showPoint(code, DataSource.MapData.getRWIS())
            MapDataType.BUS_STOP.CODE ->showPoint(code,DataSource.MapData.getBusStop())
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

    private fun createMarkerOptions(icon: Icon, latLng: LatLng): MarkerOptions {
        return MarkerOptions().setIcon(icon).setPosition(latLng)
    }

    private fun removePointFromMap(code: String) {
        markerMap[code]?.let { for (marker in it) mapBoxMap?.removeMarker(marker) }
    }

    override fun onStart() {
        super.onStart()
        locationComponent?.onStart()
    }

    override fun onStop() {
        super.onStop()
        locationComponent?.onStop()
    }

    override fun onDestroy() {
        locationComponent?.onDestroy()
        super.onDestroy()
    }
}