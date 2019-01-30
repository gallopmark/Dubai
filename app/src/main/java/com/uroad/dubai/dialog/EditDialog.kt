package com.uroad.dubai.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.library.utils.DisplayUtils

/**
 *Created by MFB on 2018/8/13.
 * 编辑框
 */
class EditDialog(private val context: Activity) : Dialog(context, R.style.EditDialog) {
    private var hint: String? = null
    private var content: String? = null
    private var buttonText: String? = null
    private var onButtonClickListener: OnButtonClickListener? = null

    fun withHint(hint: String): EditDialog {
        this.hint = hint
        return this
    }

    fun withContent(content: String): EditDialog {
        this.content = content
        return this
    }

    fun withButtonText(buttonText: String): EditDialog {
        this.buttonText = buttonText
        return this
    }

    fun withButtonClickListener(onButtonClickListener: OnButtonClickListener): EditDialog {
        this.onButtonClickListener = onButtonClickListener
        return this
    }

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        window?.let { window ->
            val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, LinearLayout(context), false)
            window.setContentView(contentView)
            val editText = contentView.findViewById<EditText>(R.id.etContent)
            editText.requestFocus()
            editText.isFocusable = true
            hint?.let { editText.hint = hint }
            content?.let {
                editText.setText(it)
                editText.setSelection(editText.text.length)
            }
            val mButton = contentView.findViewById<Button>(R.id.mButton)
            if (!TextUtils.isEmpty(buttonText)) mButton.text = buttonText
            mButton.isEnabled = !TextUtils.isEmpty(editText.text)
            if (mButton.isEnabled) {
                mButton.setBackgroundResource(R.drawable.bg_button_comment_selector)
            } else {
                mButton.setBackgroundResource(R.drawable.bg_button_comment_default_corners_5dp)
            }
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mButton.isEnabled = !TextUtils.isEmpty(s.toString().trim())
                    if (mButton.isEnabled) {
                        mButton.setBackgroundResource(R.drawable.bg_button_comment_selector)
                    } else {
                        mButton.setBackgroundResource(R.drawable.bg_button_comment_default_corners_5dp)
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            mButton.setOnClickListener {
                val content = editText.text.toString()
                onButtonClickListener?.onButtonClick(content, this@EditDialog)
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.OperateAnim)
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            window.setGravity(Gravity.BOTTOM)
        }
    }

    interface OnButtonClickListener {
        fun onButtonClick(content: String, dialog: EditDialog)
    }
}