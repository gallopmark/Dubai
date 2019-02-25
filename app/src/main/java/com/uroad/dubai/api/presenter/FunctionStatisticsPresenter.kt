package com.uroad.dubai.api.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi
import timber.log.Timber

@SuppressLint("TimberArgCount")
class FunctionStatisticsPresenter(private val context: Context)
    : BasePresenter<BaseView>() {

    fun onFuncStatistics(functionId: String?) {
        if (!UserPreferenceHelper.isLogin(context)) return
        val userUuid = UserPreferenceHelper.getUserUUID(context)
        request(WebApi.FUNCTION_STATISTICS, WebApi.functionStatisticsParams(userUuid, functionId), object : StringObserver() {

            override fun onHttpResultOk(data: String?) {
                Timber.d("statistics", GsonUtils.getDataAsString(data))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                Timber.d("statistics", "$errorMsg")
            }
        })
    }
}