package com.uroad.dubai.common

import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.uroad.dubai.R
import com.uroad.library.compat.AppToast
import com.uroad.library.compat.LoadingDialog

abstract class BaseFragment :BaseLucaFragment(){
    private var mShortToast: Toast? = null
    private var mLongToast: Toast? = null
    private var mLoadingDialog: LoadingDialog? = null

    open fun showShortToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, LinearLayout(context), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mShortToast == null) {
            mShortToast = AppToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_SHORT }
        }
        mShortToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLongToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, LinearLayout(context), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mLongToast == null) {
            mLongToast = AppToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
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
        mLoadingDialog = LoadingDialog(context).setMsg(msg).apply { show() }
    }

    open fun endLoading() {
        mLoadingDialog?.let {
            it.dismiss()
            mLoadingDialog = null
        }
    }

    override fun onDestroyView() {
        mShortToast?.cancel()
        mLongToast?.cancel()
        mLoadingDialog?.dismiss()
        super.onDestroy()
        super.onDestroyView()
    }
}