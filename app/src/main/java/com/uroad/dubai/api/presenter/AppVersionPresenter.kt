package com.uroad.dubai.api.presenter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.library.common.BaseActivity
import com.uroad.library.common.BaseLucaActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.VersionMDL
import com.uroad.dubai.service.VersionUpdateService
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.library.compat.AppDialog
import com.uroad.library.utils.VersionUtils

class AppVersionPresenter<T : BaseActivity> : BasePresenter<BaseView> {
    private var activity: T
    private var callback: BaseView? = null
    private val handler: Handler

    constructor(activity: T) {
        this.activity = activity
        handler = Handler(Looper.getMainLooper())
    }

    constructor(activity: T, callback: BaseView?) {
        this.activity = activity
        this.callback = callback
        handler = Handler(Looper.getMainLooper())
    }

    fun checkVersion() {
        request(WebApi.APP_VERSION, WebApi.getBaseParams(), object : StringObserver(callback) {
            override fun onHttpResultOk(data: String?) {
                val mdl = GsonUtils.fromDataBean(data, VersionMDL::class.java)
                mdl?.let { checkOrUpdate(it) }
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                handler.postDelayed({ checkVersion() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
            }
        })
    }

    private fun checkOrUpdate(mdl: VersionMDL) {
        var version = ""
        mdl.version?.let { version = it }
        if (VersionUtils.isNeedUpdate(version, PackageInfoUtils.getVersionName(activity))) {
            activity.showDialog(activity.getString(R.string.dialog_default_title), mdl.versioncontent,
                    activity.getString(R.string.version_update_cancel),
                    activity.getString(R.string.version_update_confirm),
                    object : BaseLucaActivity.DialogViewClickListener {
                        override fun onConfirm(v: View, dialog: AppDialog) {
                            dialog.dismiss()
                            startUpdate(mdl.url)
                        }

                        override fun onCancel(v: View, dialog: AppDialog) {
                            dialog.dismiss()
                        }
                    })
        }
    }

    private fun startUpdate(url: String?) {
        if (TextUtils.isEmpty(url)) activity.showShortToast(activity.getString(R.string.version_update_error_url))
        else {
            activity.startService(Intent(activity, VersionUpdateService::class.java).apply { putExtra("downloadUrl", url) })
        }
    }

    override fun detachView() {
        handler.removeCallbacksAndMessages(null)
        super.detachView()
    }
}