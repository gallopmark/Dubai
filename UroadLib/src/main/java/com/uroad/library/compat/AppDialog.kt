package com.uroad.library.compat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.uroad.library.R

/**
 * @author MFB
 * @create 2018/12/10
 * @describe 自定义对话框 兼容各个版本系统(仿IOS对话框)
 */
class AppDialog(mContext: Context) : Dialog(mContext, R.style.AppDialog) {

    companion object {
        fun getTipsDialog(context: Context, message: CharSequence?): AppDialog {
            val dialog = AppDialog(context)
            dialog.setTitle(context.getString(R.string.dialog_default_title))
            dialog.setMessage(message)
            dialog.hideDivider()
            return dialog
        }
    }

    private val contentView: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_appcompat, LinearLayout(mContext), false)
    private val tvTitle: TextView
    private val tvMessage: TextView
    private val tvCancel: TextView
    private val vDivider: View
    private val tvConfirm: TextView

    init {
        tvTitle = contentView.findViewById(R.id.tvTitle)
        tvMessage = contentView.findViewById(R.id.tvMessage)
        tvCancel = contentView.findViewById(R.id.tvCancel)
        vDivider = contentView.findViewById(R.id.vDivider)
        tvConfirm = contentView.findViewById(R.id.tvConfirm)
        tvMessage.movementMethod = ScrollingMovementMethod.getInstance()
    }

    override fun setTitle(title: CharSequence?) {
        if (TextUtils.isEmpty(title)) tvTitle.visibility = View.GONE
        else {
            tvTitle.text = title
            tvTitle.visibility = View.VISIBLE
        }
    }

    fun setMessage(message: CharSequence?): AppDialog {
        tvMessage.text = message
        return this
    }

    fun hideDivider(): AppDialog {
        vDivider.visibility = View.GONE
        return this
    }

    /* 确定按钮 */
    fun setPositiveButton(text: CharSequence?, onClickListener: OnClickListener?): AppDialog {
        if (!TextUtils.isEmpty(text)) {
            tvConfirm.text = text
            tvConfirm.visibility = View.VISIBLE
            tvConfirm.setOnClickListener {
                if (onClickListener != null) onClickListener.onClick(tvConfirm, this@AppDialog)
                else dismiss()
            }
        } else {
            tvConfirm.visibility = View.GONE
        }
        return this
    }

    fun setNegativeButton(negative: CharSequence?, onClickListener: OnClickListener?): AppDialog {
        if (!TextUtils.isEmpty(negative)) {
            tvCancel.text = negative
            tvCancel.visibility = View.VISIBLE
            tvCancel.setOnClickListener {
                if (onClickListener != null) onClickListener.onClick(tvCancel, this@AppDialog)
                else dismiss()
            }
        } else {
            tvCancel.visibility = View.GONE
        }
        return this
    }

    fun setTitleTextColor(color: Int): AppDialog {
        tvTitle.setTextColor(color)
        return this
    }

    fun setMessageTextColor(color: Int): AppDialog {
        tvMessage.setTextColor(color)
        return this
    }

    fun setPositiveTextColor(color: Int): AppDialog {
        tvConfirm.setTextColor(color)
        return this
    }

    fun setNegativeTextColor(color: Int): AppDialog {
        tvCancel.setTextColor(color)
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView, WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT))
    }

    interface OnClickListener {
        fun onClick(v: View, dialog: AppDialog)
    }
}