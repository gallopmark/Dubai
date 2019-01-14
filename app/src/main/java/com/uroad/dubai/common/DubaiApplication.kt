package com.uroad.dubai.common

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.R
import com.uroad.dubai.model.ScenicMDL
import java.io.File
import kotlin.properties.Delegates

class DubaiApplication : MultiDexApplication() {
    companion object {
        //25.19703, 55.274221
        val DEFAULT_LATLNG = LatLng(25.271139, 55.307485)
        const val DEFAULT_ZOOM = 12.toDouble()
        const val DEFAULT_DELAY_MILLIS = 3000L
        var clickItemScenic: ScenicMDL? = null
        lateinit var COMPRESSOR_PATH: String
    }

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(this, getString(R.string.mapBoxToken))
        initCompressorPath()
    }


    private fun initCompressorPath() {
        COMPRESSOR_PATH = "${cacheDir.absolutePath}${File.separator}compressor"
        File(COMPRESSOR_PATH).apply { if (!exists()) this.mkdirs() }
    }
}