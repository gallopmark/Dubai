package com.uroad.dubai.common

import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.library.common.BaseFragment
import com.uroad.library.utils.DeviceUtils

abstract class BaseDubaiFragment : BaseFragment() {

    fun getUserId() = UserPreferenceHelper.getUserId(context)

    fun getAndroidID() = DeviceUtils.getAndroidID(context)
}