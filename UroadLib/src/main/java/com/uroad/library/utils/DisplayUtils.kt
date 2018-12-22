package com.uroad.library.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class DisplayUtils {
    companion object {
        // 将px值转换为dip或dp值
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        // 将dip或dp值转换为px值
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        // 将px值转换为sp值
        fun px2sp(context: Context, pxValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        // 将sp值转换为px值
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        fun getWindowWidth(context: Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.widthPixels
        }

        fun getWindowHeight(context: Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.heightPixels
        }

        // 屏幕宽度（像素）
        fun getWindowWidth(context: Activity): Int {
            val metric = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metric)
            return metric.widthPixels
        }

        // 屏幕高度（像素）
        fun getWindowHeight(context: Activity): Int {
            val metric = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metric)
            return metric.heightPixels
        }

        // 状态栏高度（像素）
        fun getStatusHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
    }
}