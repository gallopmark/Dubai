package com.uroad.dubai.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.uroad.dubai.R

/**
 * @author MFB
 * @create 2018/12/10
 * @describe 自定义对话框 兼容各个版本系统(仿IOS对话框)
 */
class SexDialog(mContext: Context) : Dialog(mContext, R.style.AppDialog) {

    private val contentView: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_sex, LinearLayout(mContext), false)
    private val tvTitle: TextView
    private val tvCancel: TextView
    private val man : RadioButton
    private val female : RadioButton
    private val vDivider: View
    private val tvConfirm: TextView

    init {
        tvTitle = contentView.findViewById(R.id.tvTitle)
        tvCancel = contentView.findViewById(R.id.tvCancel)
        man = contentView.findViewById(R.id.rbMan)
        female = contentView.findViewById(R.id.rbFemale)
        vDivider = contentView.findViewById(R.id.vDivider)
        tvConfirm = contentView.findViewById(R.id.tvConfirm)
    }

    override fun setTitle(title: CharSequence?) {
        if (TextUtils.isEmpty(title)) tvTitle.visibility = View.GONE
        else {
            tvTitle.text = title
            tvTitle.visibility = View.VISIBLE
        }
    }


    fun hideDivider(): SexDialog {
        vDivider.visibility = View.GONE
        return this
    }

    fun getSex() : String{
        return when {
            man.isChecked -> "1"
            female.isChecked -> "2"
            else -> "0"
        }
    }

    /* 确定按钮 */
    fun setPositiveButton(text: CharSequence?, onClickListener: OnClickListener?): SexDialog {
        if (!TextUtils.isEmpty(text)) {
            tvConfirm.text = text
            tvConfirm.visibility = View.VISIBLE
            tvConfirm.setOnClickListener {
                if (onClickListener != null) onClickListener.onClick(tvConfirm, this@SexDialog)
                else dismiss()
            }
        } else {
            tvConfirm.visibility = View.GONE
        }
        return this
    }

    fun setNegativeButton(negative: CharSequence?, onClickListener: OnClickListener?): SexDialog {
        if (!TextUtils.isEmpty(negative)) {
            tvCancel.text = negative
            tvCancel.visibility = View.VISIBLE
            tvCancel.setOnClickListener {
                if (onClickListener != null) onClickListener.onClick(tvCancel, this@SexDialog)
                else dismiss()
            }
        } else {
            tvCancel.visibility = View.GONE
        }
        return this
    }

    fun setTitleTextColor(color: Int): SexDialog {
        tvTitle.setTextColor(color)
        return this
    }


    fun setPositiveTextColor(color: Int): SexDialog {
        tvConfirm.setTextColor(color)
        return this
    }

    fun setNegativeTextColor(color: Int): SexDialog {
        tvCancel.setTextColor(color)
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView, WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT))
    }

    interface OnClickListener {
        fun onClick(v: View, dialog: SexDialog)
    }
}