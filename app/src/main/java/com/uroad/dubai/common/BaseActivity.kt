package com.uroad.dubai.common


import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.uroad.dubai.R
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.library.compat.AppToast
import com.uroad.library.compat.LoadingDialog
import com.uroad.library.utils.DeviceUtils

/*需要toast提示时的页面继承改activity*/
abstract class BaseActivity : BaseLucaActivity() {

    private var mShortToast: Toast? = null
    private var mLongToast: Toast? = null
    private var mLoadingDialog: LoadingDialog? = null

    open fun showShortToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(this).inflate(R.layout.layout_compat_toast, LinearLayout(this), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mShortToast == null) {
            mShortToast = AppToast(this, R.style.CompatToast).apply { duration = Toast.LENGTH_SHORT }
        }
        mShortToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLongToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(this).inflate(R.layout.layout_compat_toast, LinearLayout(this), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mLongToast == null) {
            mLongToast = AppToast(this, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
        }
        mLongToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLoading() {
        showLoading("")
    }

    open fun showLoading(msg: CharSequence?) {
        mLoadingDialog?.let {
            if (it.isShowing) it.dismiss()
            mLoadingDialog = null
        }
        mLoadingDialog = LoadingDialog(this).setMsg(msg).apply { show() }
    }

    open fun endLoading() {
        mLoadingDialog?.let {
            it.dismiss()
            mLoadingDialog = null
        }
    }

    fun getUserId() = UserPreferenceHelper.getUserId(this)

    fun getTestUserId() = "201901227175316"

    fun getAndroidID() = DeviceUtils.getAndroidID(this)

    override fun onDestroy() {
        mShortToast?.cancel()
        mLongToast?.cancel()
        mLoadingDialog?.dismiss()
        super.onDestroy()
    }

    open fun drawable(id: Int) = ContextCompat.getDrawable(this@BaseActivity, id)

}