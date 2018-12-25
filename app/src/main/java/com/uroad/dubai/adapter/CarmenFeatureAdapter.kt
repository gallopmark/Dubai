package com.uroad.dubai.adapter

import android.app.Activity
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter

/**
 * @author MFB
 * @create 2018/11/14
 * @describe
 */
class CarmenFeatureAdapter(context: Activity, mDatas: MutableList<CarmenFeature>)
    : BaseArrayRecyclerAdapter<CarmenFeature>(context, mDatas) {
    override fun bindView(viewType: Int): Int = R.layout.item_carmenreature

    override fun onBindHoder(holder: RecyclerHolder, t: CarmenFeature, position: Int) {
        holder.setText(R.id.tvPlaceName, t.placeName())
    }
}