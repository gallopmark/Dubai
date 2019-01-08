package com.uroad.dubai.adapter

import android.content.Context
import android.view.View
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.PoiSearchPoiMDL
import com.uroad.dubai.model.PoiSearchTextMDL

class PoiSearchAdapter(content: Context, data: MutableList<CarmenFeature>)
    : BaseArrayRecyclerAdapter<CarmenFeature>(content, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_poisearch_poi

    override fun onBindHoder(holder: RecyclerHolder, t: CarmenFeature, position: Int) {
        holder.setText(R.id.tvText, t.placeName())
        if (position == itemCount - 1) {
            holder.setVisibility(R.id.vDivider, View.GONE)
        } else {
            holder.setVisibility(R.id.vDivider, View.VISIBLE)
        }
    }
}