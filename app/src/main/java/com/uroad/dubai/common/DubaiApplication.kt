package com.uroad.dubai.common

import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tencent.bugly.crashreport.CrashReport
import com.uroad.dubai.R
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.service.MessageTopic
import java.io.File

class DubaiApplication : MultiDexApplication() {
    companion object {
        //25.19703, 55.274221
        lateinit var instance: DubaiApplication
        val DEFAULT_LATLNG = LatLng(25.271139, 55.307485)
        const val DEFAULT_ZOOM = 10.toDouble()
        const val DEFAULT_DELAY_MILLIS = 3000L
        var clickItemScenic: ScenicMDL? = null
        lateinit var COMPRESSOR_PATH: String
        fun getUserId(): String {
            return UserPreferenceHelper.getUserId(instance) ?: ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initMapbox()
        initFirebase()
        initCompressorPath()
        initBugly()
    }

    private fun initMapbox() {
        Mapbox.getInstance(this, getString(R.string.mapBoxToken))
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("${MessageTopic.EVENT.CODE}${getUserId()}")
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.NEWS.CODE).addOnCompleteListener {
            if(it.isSuccessful){
                Log.e("firebase","successful")
            } else {
                Log.e("firebase","failure")
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.NOTICE.CODE)
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.SYSTEM.CODE)
    }

    private fun initCompressorPath() {
        COMPRESSOR_PATH = "${cacheDir.absolutePath}${File.separator}compressor"
        File(COMPRESSOR_PATH).apply { if (!exists()) this.mkdirs() }
    }

    /*init bugly*/
    private fun initBugly() {
        if (DubaiConfig.isDebug) return
        CrashReport.initCrashReport(this, getString(R.string.BuglyAppID), false)
    }
}