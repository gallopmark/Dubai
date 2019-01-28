package com.uroad.dubai.fragment

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.common.BasePresenterFragment

/**
 * @author MFB
 * @create 2019/1/28
 * @describe
 */
abstract class NearMeBaseFragment<P : BasePresenter<*>> : BasePresenterFragment<P>() {

    var longitude: Double = 0.0
    var latitude: Double = 0.0

    open fun update(longitude: Double, latitude: Double) {
        this.longitude = longitude
        this.latitude = latitude
        initData()
    }

}