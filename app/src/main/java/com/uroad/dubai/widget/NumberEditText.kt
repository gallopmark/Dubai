package com.uroad.dubai.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import com.uroad.dubai.R

/**
 *
 * 方格数字输入控件
 *
 * @create by thinkin
 * e7yoo.com
 * @email thinkin.liu@gmail.com
 */
class NumberEditText : LinearLayout {

    /** 输入框  */
    private val inputTv = arrayOfNulls<TextView>(6)
    /** 用于接受键盘输入内容  */
    private var invisibleEt: EditText? = null
    /** 输入结束监听  */
    private var onInputFinishListener: OnInputFinishListener? = null

    constructor(context: Context) : super(context) {
        createView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        createView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createView(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        createView(context)
    }

    private fun createView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.e7yoo_number_edittext, null)
        inputTv[0] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv0) as TextView
        inputTv[1] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv1) as TextView
        inputTv[2] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv2) as TextView
        inputTv[3] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv3) as TextView
        inputTv[4] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv4) as TextView
        inputTv[5] = view.findViewById<View>(R.id.e7yoo_number_edittext_tv5) as TextView
        invisibleEt = view.findViewById<View>(R.id.e7yoo_number_edittext_et) as EditText
        invisibleEt!!.addTextChangedListener(InvisibleEtTextWatcher())
        addView(view)
    }

    fun setOnInputFinish(listener: OnInputFinishListener) {
        this.onInputFinishListener = listener
    }

    fun clearText() {
        val tvLength = inputTv.size
        for (i in 0 until tvLength) {
            inputTv[i]?.text = ""
        }
        invisibleEt!!.setText("")
    }

    fun condition(): Boolean {
        return invisibleEt!!.text.toString().trim { it <= ' ' }.length == inputTv.size
    }

    fun setText(code : String){
        invisibleEt?.setText(code)
    }

    /**
     * 处理从edittext到textview
     */
    private inner class InvisibleEtTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            val text = s.toString().trim { it <= ' ' }
            val chars = text.toCharArray()
            val length = chars.size
            val tvLength = inputTv.size
            for (i in 0 until tvLength) {
                if (i < length) {
                    inputTv[i]?.text = "" + chars[i]
                } else {
                    inputTv[i]?.text = ""
                }
            }
            if (length == tvLength && onInputFinishListener != null) {
                onInputFinishListener!!.onInputFinish(text)
            }
        }
    }

    interface OnInputFinishListener {
        fun onInputFinish(text: String)
    }

}
