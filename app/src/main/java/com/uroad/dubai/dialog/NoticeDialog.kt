package com.uroad.dubai.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.model.NoticeMDL

class NoticeDialog(private val mContext: Context, private val mdl: NoticeMDL)
    : Dialog(mContext, R.style.AppDialog) {

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        window?.let { window ->
            val contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_notice, LinearLayout(mContext), false)
            val mTimeTextView = contentView.findViewById<TextView>(R.id.mTimeTextView)
            val mCloseImageView = contentView.findViewById<ImageView>(R.id.mCloseImageView)
            val mContentTextView = contentView.findViewById<TextView>(R.id.mContentTextView)
            mCloseImageView.setOnClickListener { dismiss() }
            mTimeTextView.text = mdl.publishtime
            mContentTextView.text = mdl.content
            window.setContentView(contentView)
            window.setGravity(Gravity.CENTER)
        }
    }
}