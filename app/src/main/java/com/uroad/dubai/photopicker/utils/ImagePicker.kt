package com.uroad.dubai.photopicker.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.uroad.dubai.photopicker.ui.PhotoPickerActivity

class ImagePicker private constructor(private val tag: Any) {

    private var isMultiple = false //选择模式（单选或多选）
    private var limit = 1   //最大图片选择数量
    private var crop = false    //是否需要裁剪
    private var isSaveRectangle = true  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private var isCompress = false   //是否对图片压缩处理
    private var outPutX = 800        //裁剪保存宽度
    private var outPutY = 800           //裁剪保存高度
    private var focusWidth = 800         //焦点框的宽度
    private var focusHeight = 800        //焦点框的高度
    private var requestCode: Int = 1

    companion object {
        const val EXTRA_PATHS = "extra_paths"
        fun with(`object`: Any): ImagePicker {
            return ImagePicker(`object`)
        }
    }

    fun isMultipleChoice(limit: Int): ImagePicker {
        this.isMultiple = true
        if (limit <= 1) this.limit = 1
        else this.limit = limit
        return this
    }

    fun isCompress(isCompress: Boolean): ImagePicker {
        this.isCompress = isCompress
        return this
    }

    fun rectangleCrop(): ImagePicker {
        this.crop = true
        this.isSaveRectangle = true
        return this
    }

    fun circleCrop(): ImagePicker {
        this.crop = true
        this.isSaveRectangle = false
        return this
    }

    fun outPutX(outPutX: Int): ImagePicker {
        this.outPutX = outPutX
        return this
    }

    fun outPutY(outPutY: Int): ImagePicker {
        this.outPutY = outPutY
        return this
    }

    fun focusWidth(focusWidth: Int): ImagePicker {
        this.focusWidth = focusWidth
        return this
    }

    fun focusHeight(focusHeight: Int): ImagePicker {
        this.focusHeight = focusHeight
        return this
    }

    fun requestCode(requestCode: Int): ImagePicker {
        this.requestCode = requestCode
        return this
    }

    fun start() {
        if (tag is Activity) {
            val activity = tag
            val intent = Intent(activity, PhotoPickerActivity::class.java)
            intent.putExtras(getBundle())
            activity.startActivityForResult(intent, requestCode)
        } else if (tag is Fragment) {
            val fragment = tag
            if (fragment.activity == null) return
            val intent = Intent(fragment.activity, PhotoPickerActivity::class.java)
            intent.putExtras(getBundle())
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    private fun getBundle() = Bundle().apply {
        putBoolean("isMultiple", isMultiple)
        putInt("limit", limit)
        putBoolean("isCompress", isCompress)
        putBoolean("isCrop", crop)
        putBoolean("isSaveRectangle", isSaveRectangle)
        putInt("outPutX", outPutX)
        putInt("outPutY", outPutY)
        putInt("focusWidth", focusWidth)
        putInt("focusHeight", focusHeight)
    }
}