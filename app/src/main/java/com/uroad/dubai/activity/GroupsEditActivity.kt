package com.uroad.dubai.activity

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CarmenFeatureAdapter
import com.uroad.dubai.api.presenter.GroupsEditPresenter
import com.uroad.dubai.api.view.GroupsEditView
import com.uroad.dubai.common.BaseMapBoxActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.library.utils.InputMethodUtils
import kotlinx.android.synthetic.main.activity_groupsedit.*

/**
 * @author MFB
 * @create 2019/1/9
 * @describe create a group
 */
class GroupsEditActivity : BaseMapBoxActivity(), GroupsEditView {

    private lateinit var presenter: GroupsEditPresenter
    private var poiKey: String? = ""
    private val handler = Handler()
    private val features = ArrayList<CarmenFeature>()
    private lateinit var featureAdapter: CarmenFeatureAdapter
    private var isItemSelected = false
    private var destination: CarmenFeature? = null
    private var destinationMarker: Marker? = null
    private var teamName: String? = null
    override fun setBaseMapBoxView(): Int = R.layout.activity_groupsedit

    override fun onMapSetUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.groups_create))
        withOption(getString(R.string.confirm), View.OnClickListener { whenConfirm() })
        presenter = GroupsEditPresenter(this, this)
        initDestination()
        initRvPoi()
    }

    override fun onMapAsync(mapBoxMap: MapboxMap) {

    }

    private fun initDestination() {
        etDestination.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
                val content = cs.toString()
                if (TextUtils.isEmpty(content)) {
                    whenContentEmpty()
                } else {
                    if (isItemSelected) isItemSelected = false
                    else handleWhenInput(content)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private val poiSearchRun = Runnable {
        presenter.cancelCall()
        poiKey?.let { presenter.doPoiSearch(it) }
    }

    private fun whenContentEmpty() {
        onInitialState()
    }

    private fun onInitialState() {
        destination = null
        rvPoi.visibility = View.GONE
        handler.removeCallbacks(poiSearchRun)
    }

    private fun handleWhenInput(content: String) {
        onInitialState()
        poiKey = content
        handler.postDelayed(poiSearchRun, 500)
    }

    private fun initRvPoi() {
        featureAdapter = CarmenFeatureAdapter(this, features).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until features.size) {
                        val target = features[position]
                        isItemSelected = true
                        etDestination.setText(target.placeName())
                        etDestination.setSelection(etDestination.text.length)
                        target.center()?.let { moveCamera(target, it) }
                        rvPoi.visibility = View.GONE
                    }
                }
            })
        }
        rvPoi.adapter = featureAdapter
    }

    private fun moveCamera(target: CarmenFeature, point: Point) {
        InputMethodUtils.hideSoftInput(this)
        destination = target
        val position = LatLng(point.latitude(), point.longitude())
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10.toDouble()))
        destinationMarker?.let { mapBoxMap?.removeMarker(it) }
        destinationMarker = mapBoxMap?.addMarker(MarkerOptions().position(position).icon(IconFactory.getInstance(this).fromResource(R.mipmap.ic_destination_red)))
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
    }

    override fun onPoiResult(features: MutableList<CarmenFeature>) {
        if (features.size > 0) {
            this.features.clear()
            this.features.addAll(features)
            featureAdapter.notifyDataSetChanged()
            rvPoi.visibility = View.VISIBLE
        }
    }

    private fun whenConfirm() {
        teamName = etTeamName.text.toString()
        when {
            TextUtils.isEmpty(teamName) -> showShortToast(etTeamName.hint)
            destination == null -> showShortToast(etDestination.hint)
            else -> createGroup(teamName, destination?.center()?.latitude(), destination?.center()?.longitude())
        }
    }

    private fun createGroup(teamName: String?, latitude: Double?, longitude: Double?) {
        presenter.createGroup(teamName, latitude, longitude)
    }

    override fun onCreateGroupResult() {
        openActivity(GroupsDetailActivity::class.java, Bundle().apply {
            putString("teamName", teamName)
            putString("destination", destination?.toJson())
        })
        finish()
    }
}