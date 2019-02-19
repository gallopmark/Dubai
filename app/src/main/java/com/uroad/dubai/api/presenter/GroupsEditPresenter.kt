package com.uroad.dubai.api.presenter

import android.content.Context
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.GroupsEditView

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