package com.uroad.library.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.app.Activity


class InputMethodUtils {
    companion object {
        fun showSoftInput(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        fun hideSoftInput(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0) //强制隐藏键盘
        }

        fun hideSoftInput(context: Activity) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (context.currentFocus != null && context.currentFocus.windowToken != null) {
                imm.hideSoftInputFromWindow(context.currentFocus.windowToken, 0)
            }
        }
    }
}