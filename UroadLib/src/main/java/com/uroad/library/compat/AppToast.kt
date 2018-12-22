package com.uroad.library.compat

import android.content.Context
import android.view.WindowManager
import android.widget.Toast

/**
 * @author MFB
 * @create 2018/12/10
 * @describe 自定义Toast 兼容各个系统
 */
class AppToast(context: Context, anim: Int) : Toast(context) {
    init {
        val clazz = Toast::class.java
        try {
            val mTN = clazz.getDeclaredField("mTN")
            mTN.isAccessible = true
            val mObj = mTN.get(this)
            val field = mObj.javaClass.getDeclaredField("mParams")
            if (field != null) {
                field.isAccessible = true
                val mParams = field.get(mObj)
                if (mParams != null && mParams is WindowManager.LayoutParams) {
                    mParams.windowAnimations = anim
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}