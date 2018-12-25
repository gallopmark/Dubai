package com.uroad.dubai.activity

import android.annotation.SuppressLint
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
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions


/**
 * @author MFB
 * @create 2018/12/18
 * @describe Road navigation
 */
class RoadNavigationActivity : BaseNoTitleMapBoxActivity(), PermissionsListener {

    private class RoadData {
        /**
         * NSArray * latArray = @[@(24.29045862222854),@(25.71109733694287)
         * ,@(25.50251457879257),@(25.260430807520947),@(24.98663128116354)
         * ,@(24.861623304922887),@(24.583673073761176),@(24.65789600887483)
         * ,@(25.035503774803118),@(25.012879832914024),@(24.604357106770124)
         * ,@(24.628219646337257),@(25.235800209105093),@(24.4970057134079)
         * ,@(25.151713168516054),@(25.47217244601599),@(24.919234574159304)
         * ,@(24.218204151399064),@(24.18581475318689),@(24.20949902407014)
         * ,@(25.303561875079183), @(25.473265821079735), @(24.423435007145386)
         * , @(25.115663223796332)];
        NSArray * logArray = @[@(55.852681624449815),@(55.984812838019934)
        ,@(56.223575439742945),@(56.18408021591233),@(56.13103272127739)
        ,@(56.069466637252845),@(55.74578456977292),@(55.69731953832229)
        ,@(55.59616854894591),@(55.28375285528648),@(55.13522740529925)
        ,@(54.74918917311584),@(55.32494669342714),@(55.43882546263342)
        ,@(55.8957487204126),@(56.009015219291996),@(56.11049688466619)
        ,@(55.5668175990312),@(55.26069186566269),@(54.41366434182248)
        ,@(55.4707649820881), @(55.58799714838494), @(55.02788501046564)
        , @(55.19529231879494)];
         */
        companion object {
            val latitudeArray = doubleArrayOf(24.29045862222854, 25.71109733694287, 25.50251457879257,
                    25.260430807520947, 24.98663128116354, 24.861623304922887, 24.583673073761176, 24.65789600887483,
                    25.035503774803118, 25.012879832914024, 24.604357106770124, 24.628219646337257, 25.235800209105093,
                    24.4970057134079, 25.151713168516054, 5.47217244601599, 24.919234574159304, 24.218204151399064,
                    24.18581475318689, 24.20949902407014, 25.303561875079183, 25.473265821079735, 24.423435007145386, 25.115663223796332)
            val longitudeArray = doubleArrayOf(55.852681624449815, 55.984812838019934, 56.223575439742945,
                    56.18408021591233, 56.13103272127739, 56.069466637252845, 55.74578456977292, 55.69731953832229,
                    55.59616854894591, 55.28375285528648, 55.13522740529925, 54.74918917311584, 55.32494669342714,
                    55.43882546263342, 55.8957487204126, 56.009015219291996, 56.11049688466619, 55.5668175990312,
                    55.26069186566269, 54.41366434182248, 55.4707649820881, 55.58799714838494, 55.02788501046564, 55.19529231879494)

            fun getAccident(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[0], longitudeArray[0]))
                add(LatLng(latitudeArray[1], longitudeArray[1]))
                add(LatLng(latitudeArray[2], longitudeArray[2]))
            }

            fun getConstruction(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[3], longitudeArray[3]))
                add(LatLng(latitudeArray[4], longitudeArray[4]))
                add(LatLng(latitudeArray[5], longitudeArray[5]))
            }

            fun getParking(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[6], longitudeArray[6]))
                add(LatLng(latitudeArray[7], longitudeArray[7]))
                add(LatLng(latitudeArray[8], longitudeArray[8]))
            }

            fun getCCTV(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[9], longitudeArray[8]))
                add(LatLng(latitudeArray[10], longitudeArray[10]))
                add(LatLng(latitudeArray[11], longitudeArray[11]))
            }

            fun getDMS(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[12], longitudeArray[12]))
                add(LatLng(latitudeArray[13], longitudeArray[13]))
                add(LatLng(latitudeArray[14], longitudeArray[14]))
            }

            fun getPolice(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[15], longitudeArray[15]))
                add(LatLng(latitudeArray[16], longitudeArray[16]))
                add(LatLng(latitudeArray[17], longitudeArray[17]))
            }

            fun getWeather(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[18], longitudeArray[18]))
                add(LatLng(latitudeArray[19], longitudeArray[19]))
                add(LatLng(latitudeArray[20], longitudeArray[20]))
            }

            fun getRWIS(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[21], longitudeArray[21]))
                add(LatLng(latitudeArray[22], longitudeArray[21]))
                add(LatLng(latitudeArray[23], longitudeArray[21]))
            }
        }
    }

    private var statusHeight = 0
    private var isMapAsync = false
    private var fillExtrusionLayer: FillExtrusionLayer? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var userMarker: Marker? = null
    private val markerMap = ArrayMap<String, MutableList<Marker>>()

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
//        enableLocationComponent()
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
                R.id.cbEvents -> {

                }
                R.id.cbParking -> {
                }
                R.id.cbCCTV -> {
                }
                R.id.cbDMS -> {
                }
                R.id.cbPolice -> {

                }
                R.id.cbWeather -> {

                }
            }
        }
        cbEvents.setOnCheckedChangeListener(onCheckChangedListener)
        cbParking.setOnCheckedChangeListener(onCheckChangedListener)
        cbCCTV.setOnCheckedChangeListener(onCheckChangedListener)
        cbDMS.setOnCheckedChangeListener(onCheckChangedListener)
        cbPolice.setOnCheckedChangeListener(onCheckChangedListener)
        cbWeather.setOnCheckedChangeListener(onCheckChangedListener)
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