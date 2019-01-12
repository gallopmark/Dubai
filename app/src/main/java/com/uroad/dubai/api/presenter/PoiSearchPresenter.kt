package com.uroad.dubai.api.presenter

import android.content.Context
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.PoiSearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class PoiSearchPresenter(private val context: Context,
                              private var poiSearchView: PoiSearchView?)
    : BasePresenter<PoiSearchView>(poiSearchView) {
    private var geoClient: MapboxGeocoding? = null

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

    fun cancelCall() {
        geoClient?.cancelCall()
    }

    override fun detachView() {
        cancelCall()
        super.detachView()
    }
}