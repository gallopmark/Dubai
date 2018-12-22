package com.uroad.dubai.common

import android.support.multidex.MultiDexApplication
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R

class DubaiApplication : MultiDexApplication() {
    companion object {
        //25.19703, 55.274221
        val DEFAULT_LATLNG = LatLng(25.271139, 55.307485)
        const val DEFAULT_ZOOM = 12.toDouble()
    }

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(this, getString(R.string.mapBoxToken))
    }
}