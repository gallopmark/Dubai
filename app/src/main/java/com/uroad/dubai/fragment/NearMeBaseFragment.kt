package com.uroad.dubai.fragment

import android.os.Handler
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication

/**
 * @author MFB
 * @create 2019/1/28
 * @describe
 */
abstract class NearMeBaseFragment<P : BasePresenter<*>> : BasePresenterFragment<P>() {
    val handler = Handler()
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    open fun update(longitude: Double, latitude: Double) {
        this.longitude = longitude
        this.latitude = latitude
        initData()
    }

    open fun onRetry() {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}