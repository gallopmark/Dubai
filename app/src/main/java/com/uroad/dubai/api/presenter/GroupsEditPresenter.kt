package com.uroad.dubai.api.presenter

import android.content.Context
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.GroupsEditView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupsEditPresenter(context: Context, private var editView: GroupsEditView?)
    : BasePresenter<GroupsEditView>(editView) {

    private val presenter = PoiSearchPresenter(context, editView)

    fun doPoiSearch(content: String): MapboxGeocoding {
        return presenter.doPoiSearch(content)
    }

    fun createGroup(teamName: String?, latitude: Double?, longitude: Double?) {
        editView?.onCreateGroupResult()
    }

    fun cancelCall() {
        presenter.cancelCall()
    }
}