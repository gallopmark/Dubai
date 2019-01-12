package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.view.View
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseMapBoxActivity
import kotlinx.android.synthetic.main.activity_groupsdetail.*

/**
 * @author MFB
 * @create 2019/1/10
 * @describe groups detail
 */
class GroupsDetailActivity : BaseMapBoxActivity() {
    private var destination: CarmenFeature? = null
    private var destinationMarker: Marker? = null
    private var userMarker: Marker? = null
    override fun setBaseMapBoxView(): Int = R.layout.activity_groupsdetail

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_groupsdetail)
        withTitle("Benjamin’s group")
        withOption(getString(R.string.groups_modify), View.OnClickListener { showTipsDialog(getString(R.string.developing)) })
        initBundle()
        initView()
    }

    private fun initBundle() {
        val teamName = intent.extras?.getString("teamName")
        val destination = intent.extras?.getString("destination")
        destination?.let { this.destination = CarmenFeature.fromJson(it) }
        withTitle(teamName)
        tvDestination.text = this.destination?.placeName()
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {
        this.destination?.center()?.let { moveCamera(it) }
    }

    private fun moveCamera(point: Point) {
        val position = LatLng(point.latitude(), point.longitude())
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10.toDouble()))
        destinationMarker?.let { mapBoxMap?.removeMarker(it) }
        destinationMarker = mapBoxMap?.addMarker(MarkerOptions().position(position).icon(IconFactory.getInstance(this).fromResource(R.mipmap.ic_destination_red)))
    }

    private fun initView() {
        ivLocation.setOnClickListener { openLocation() }
        ivScenic.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivTraffic.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvInvite.setOnClickListener { openActivity(GroupsInviteActivity::class.java) }
        tvNavigation.setOnClickListener { openActivity(RouteNavigationActivity::class.java, Bundle().apply { putString("destination", destination?.toJson()) }) }
    }

    override fun onDismissLocationPermission() {
        showShortToast("user location permission not granted")
    }

    override fun onExplanationLocationPermission(permissionsToExplain: MutableList<String>?) {
        openSettings()
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
        var zoom = 10.toDouble()
        mapBoxMap?.cameraPosition?.let { zoom = it.zoom }
        val position = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(zoom).build()
        mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }
}