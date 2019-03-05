package com.uroad.dubai.common

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.support.multidex.MultiDexApplication
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tencent.bugly.crashreport.CrashReport
import com.uroad.dubai.R
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.model.UserMDL
import com.uroad.dubai.push.MessageTopic
import com.uroad.dubai.utils.AndroidBase64Utils
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.dubai.webService.ApiService
import com.uroad.dubai.webService.WebApi
import com.uroad.http.HttpOptions
import com.uroad.http.RetrofitManager
import com.uroad.library.utils.DeviceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.File

class DubaiApplication : MultiDexApplication() {
    companion object {
        //25.19703, 55.274221
        private var compositeDisposable: CompositeDisposable = CompositeDisposable()
        lateinit var instance: DubaiApplication
        var userStatus: Boolean = false
        val DEFAULT_LATLNG = LatLng(25.271139, 55.307485)
        const val DEFAULT_ZOOM = 10.toDouble()
        const val DEFAULT_DELAY_MILLIS = 3000L
        lateinit var COMPRESSOR_PATH: String
        fun getUserId(): String {
            return UserPreferenceHelper.getUserId(instance) ?: ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initApiService()
        initMapbox()
//        initFirebase()
        initCompressorPath()
        initBugly()
        validationDevice()
    }

    private fun initApiService() {
        val headers = HashMap<String, String>().apply {
            put("x-app-type", "Android")
            put("x-user-uuid", UserPreferenceHelper.getUserUUID(this@DubaiApplication) ?: "")
            put("x-app-version", PackageInfoUtils.getVersionName(this@DubaiApplication))
            put("x-device-uuid", DeviceUtils.getAndroidID(this@DubaiApplication))
            put("x-device-info", DeviceUtils.getModel())
            put("x-device-systemversion", DeviceUtils.getSystemVersion())
        }
        val options = HttpOptions.Builder()
                .writeTimeout(30 * 1000L)
                .connectTimeout(10 * 1000L)
                .readTimeout(10 * 1000L)
                .headers(headers)
                .build()
        RetrofitManager.init(ApiService.BASE_URL, options)
    }

    private fun initMapbox() {
        Mapbox.getInstance(this, getString(R.string.mapBoxToken))
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic("fcm_test")
        FirebaseMessaging.getInstance().subscribeToTopic("${MessageTopic.EVENT.CODE}${getUserId()}")
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.NEWS.CODE)
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

    private fun validationDevice() {
        val disposable = ApiService.doRequest(WebApi.VALIDATIONDEVICE,
                WebApi.validationDevice(DeviceUtils.getAndroidID(this@DubaiApplication)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val string = AndroidBase64Utils.decodeToString(it)
                    val jsonObject = JSONObject(string)
                    if (GsonUtils.isResultOk(string)) {
                        handler.removeCallbacksAndMessages(null)
                        userStatus = jsonObject.getBoolean("status")
                        val userMDL = GsonUtils.fromDataBean(string, UserMDL::class.java)
                        UserPreferenceHelper.save(this@DubaiApplication, userMDL)
                    } else {
                        handler.sendEmptyMessageDelayed(10, DEFAULT_DELAY_MILLIS)
                    }
                }, {})
        compositeDisposable.add(disposable)
    }

    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            validationDevice()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        compositeDisposable.dispose()
    }
}