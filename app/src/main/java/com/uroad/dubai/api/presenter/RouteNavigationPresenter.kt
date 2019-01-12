package com.uroad.dubai.api.presenter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.widget.PopupWindowCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CarmenFeatureAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.RouteNavigationView
import com.uroad.library.utils.DisplayUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteNavigationPresenter(private val context: Context,
                               private val naviView: RouteNavigationView)
    : BasePresenter<RouteNavigationView>(naviView) {

    private val presenter = PoiSearchPresenter(context, naviView)
    private var directions: MapboxDirections? = null

    fun doPoiSearch(content: String): MapboxGeocoding {
        return presenter.doPoiSearch(content)
    }

    fun getRoutes(origin: Point, destination: Point, profile: String) {
        naviView.onShowLoading()
        directions = buildDirections(origin, destination, profile).apply {
            enqueueCall(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    naviView.onHideLoading()
                    naviView.onNavigationRoutes(response.body()?.routes())
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    naviView.onHideLoading()
                    naviView.onShowError("")
                }
            })
        }
    }

    private fun buildDirections(origin: Point, destination: Point, profile: String): MapboxDirections {
        return MapboxDirections.builder()
                .profile(profile)
                .origin(origin)
                .destination(destination)
                .annotations(DirectionsCriteria.ANNOTATION_CONGESTION, DirectionsCriteria.ANNOTATION_DISTANCE)
                .accessToken(context.getString(R.string.mapBoxToken))
                .steps(true)
                .continueStraight(true)
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .alternatives(true)
                .roundaboutExits(true)
                .build()
    }

    fun showPoiWindow(parent: View, features: MutableList<CarmenFeature>, onItemClickListener: BaseRecyclerAdapter.OnItemClickListener): PopupWindow {
        val recyclerView = RecyclerView(context).apply {
            setBackgroundColor(ContextCompat.getColor(this@RouteNavigationPresenter.context, R.color.white))
            layoutManager = LinearLayoutManager(this@RouteNavigationPresenter.context).apply { orientation = LinearLayoutManager.VERTICAL }
        }
        val popupWindow = PopupWindow(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = false
            setBackgroundDrawable(ColorDrawable())
            isOutsideTouchable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val location = IntArray(2)
                parent.getLocationInWindow(location)
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) { // 7.1 版本处理
                    val screenHeight = DisplayUtils.getWindowHeight(context)
                    height = screenHeight - location[1] - parent.height
                }
                showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + parent.height)
            } else
                PopupWindowCompat.showAsDropDown(this, parent, 0, 0, Gravity.NO_GRAVITY)
        }
        recyclerView.adapter = CarmenFeatureAdapter(context, features).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    onItemClickListener.onItemClick(adapter, holder, view, position)
                }
            })
        }
        return popupWindow
    }

    override fun detachView() {
        directions?.cancelCall()
        super.detachView()
    }
}