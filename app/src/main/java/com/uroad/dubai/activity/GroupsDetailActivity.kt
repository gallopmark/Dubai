package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
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
import com.uroad.dubai.api.presenter.GroupsPresenter
import com.uroad.dubai.common.BaseMapBoxActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.CarTeamDataMDL
import kotlinx.android.synthetic.main.activity_groupsdetail.*

/**
 * @author MFB
 * @create 2019/1/10
 * @describe groups detail
 */
class GroupsDetailActivity : BaseMapBoxActivity(), GroupsPresenter.OnGetCarTeamCallback {
    private var teamId: String? = null
    private var destination: CarmenFeature? = null
    private var destinationMarker: Marker? = null
    private var userMarker: Marker? = null
    private lateinit var presenter: GroupsPresenter
    private lateinit var handler: Handler

    override fun setBaseMapBoxView(): Int = R.layout.activity_groupsdetail

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_groupsdetail)
        initBundle()
        initView()
        presenter = GroupsPresenter()
        handler = Handler()
    }

    private fun initBundle() {
        teamId = intent.extras?.getString("teamId")
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {
        getCarTeamData()
    }

    private fun getCarTeamData() {
        presenter.getCarTeamDataWithId(teamId, this)
    }

    override fun onGetCarTeamData(mdl: CarTeamDataMDL?) {
        withTitle(mdl?.team_data?.teamname)
        tvDestination.text = mdl?.team_data?.toplace
        mdl?.team_data?.point()?.let { moveCamera(it) }
        if (isHeader(mdl?.teammember)) {
            val bundle = Bundle().apply {
                putString("teamId", teamId)
                putString("point", mdl?.team_data?.point()?.toJson())
                putString("teamName", mdl?.team_data?.teamname)
                putString("toPlace", mdl?.team_data?.toplace)
            }
            withOption(getString(R.string.groups_modify), View.OnClickListener { openActivity(GroupsEditActivity::class.java, bundle) })
        }
    }

    private fun isHeader(members: MutableList<CarTeamDataMDL.TeamMember>?): Boolean {
        members ?: return false
        var isHeader = false
        for (member in members) {
            if (member.isown == 1 && TextUtils.equals(member.useruuid, getUserUUID())) {
                isHeader = true
                break
            }
        }
        return isHeader
    }

    override fun onShowError(errorMsg: String?) {
        handler.postDelayed({ getCarTeamData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
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

    override fun onDestroy() {
        presenter.detachView()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}