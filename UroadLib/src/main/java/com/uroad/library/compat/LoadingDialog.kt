package com.uroad.library.compat

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.uroad.library.R
import com.uroad.library.widget.CircularProgressView

class LoadingDialog(private val mContext: Activity) : Dialog(mContext, R.style.TranslucentDialog) {
    private val contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, LinearLayout(mContext), false)
    private val progressBar: CircularProgressView
    private val mLoadingTv: TextView

    init {
        progressBar = contentView.findViewById(R.id.progressBar)
        mLoadingTv = contentView.findViewById(R.id.mLoadingTv)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView, WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT))
    }

    fun setMsg(msg: CharSequence?): LoadingDialog {
        if (TextUtils.isEmpty(msg)) {
            mLoadingTv.text = mContext.getString(R.string.dialog_loading)
        } else {
            mLoadingTv.text = msg
        }
        return this
    }
}