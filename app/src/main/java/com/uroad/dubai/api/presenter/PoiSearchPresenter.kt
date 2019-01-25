package com.uroad.dubai.api.presenter

import android.content.Context
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.adapter.PoiSearchHistoryAdapter
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.PoiSearchView
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.MultiItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class PoiSearchPresenter(private val context: Context,
                              private var poiSearchView: PoiSearchView?)
    : BasePresenter<PoiSearchView>(poiSearchView) {
    private var geoClient: MapboxGeocoding? = null

    fun getPoiSearchHistoryAdapter(data: MutableList<MultiItem>, onItemClickListener: BaseRecyclerAdapter.OnItemClickListener): PoiSearchHistoryAdapter {
        return PoiSearchHistoryAdapter(context, data).apply { setOnItemClickListener(onItemClickListener) }
    }

    fun getPoiSearchAdapter(data: MutableList<CarmenFeature>, onItemClickListener: BaseRecyclerAdapter.OnItemClickListener): PoiSearchAdapter {
        return PoiSearchAdapter(context, data).apply { setOnItemClickListener(onItemClickListener) }
    }

    fun doPoiSearch(content: String): MapboxGeocoding {
        return buildClient(content).apply {
            enqueueCall(object : Callback<GeocodingResponse> {
                override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                    response.body()?.features()?.let { poiSearchView?.onPoiResult(it) }
                }

                override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {

                }
            })
        }
    }

    private fun buildClient(content: String): MapboxGeocoding {
        return MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.mapBoxToken))
//                .country("ae")
                .limit(10)
                .query(content).build()
    }

    fun doPoiSearch(point: Point, callback: PointSearchCallback): MapboxGeocoding {
        return buildClient(point).apply {
            enqueueCall(object : Callback<GeocodingResponse> {
                override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                    response.body()?.features()?.let { callback.onPointResult(it) }
                }

                override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {

                }
            })
        }
    }

    private fun buildClient(point: Point): MapboxGeocoding {
        return MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.mapBoxToken))
                .query(point)
                .build()
    }

    fun cancelCall() {
        geoClient?.cancelCall()
    }

    interface PointSearchCallback {
        fun onPointResult(features: MutableList<CarmenFeature>)
    }

    override fun detachView() {
        cancelCall()
        super.detachView()
    }
}