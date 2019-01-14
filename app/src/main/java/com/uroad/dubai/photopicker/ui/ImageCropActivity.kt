package com.uroad.dubai.photopicker.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.photopicker.widget.CropImageView
import kotlinx.android.synthetic.main.activity_photopicker_crop.*
import java.io.File

class ImageCropActivity : BaseActivity(), CropImageView.OnBitmapSaveListener {
    private var cropPath: String? = null
    private var mBitmap: Bitmap? = null
    private var isSaveRectangle: Boolean = false
    private var outPutX: Int = 800
    private var outPutY: Int = 800
    private var focusWidth = 250         //焦点框的宽度
    private var focusHeight = 250        //焦点框的高度

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_photopicker_crop)
        withTitle(getString(R.string.photopicker_crop_title))
        withOption(getString(R.string.photopicker_crop_finish), View.OnClickListener { saveImage() })
        initOptions()
        initBundle()
        initView()
    }

    private fun initOptions() {
        focusWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, resources.displayMetrics).toInt()
        focusHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, resources.displayMetrics).toInt()
    }

    private fun initBundle() {
        //获取需要的参数
        intent.extras?.let {
            cropPath = it.getString("cropPath")
            outPutX = it.getInt("outPutX")
            outPutY = it.getInt("outPutY")
            isSaveRectangle = it.getBoolean("isSaveRectangle")
            focusWidth = it.getInt("focusWidth")
            focusHeight = it.getInt("focusHeight")
        }
    }

    private fun initView() {
        cropImageView.setFocusWidth(focusWidth)
        cropImageView.setFocusHeight(focusHeight)
        if (isSaveRectangle) cropImageView.setFocusStyle(CropImageView.Style.RECTANGLE)
        else cropImageView.setFocusStyle(CropImageView.Style.CIRCLE)
        //缩放图片
        cropPath?.let {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(it, options)
            val displayMetrics = resources.displayMetrics
            options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels)
            options.inJustDecodeBounds = false
            mBitmap = BitmapFactory.decodeFile(it, options)
            cropImageView.setImageBitmap(mBitmap)
            cropImageView.setOnBitmapSaveCompleteListener(this@ImageCropActivity)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                width / reqWidth
            } else {
                height / reqHeight
            }
        }
        return inSampleSize
    }

    private fun saveImage() {
        val cropFile = File(externalCacheDir, "/crop/")
        cropImageView.saveBitmapToFile(cropFile, outPutX, outPutY, isSaveRectangle)
    }

    override fun onBitmapSaveStart() {
        showLoading(getString(R.string.photopicker_saving))
    }

    override fun onBitmapSaveSuccess(file: File) {
        endLoading()
        onResult(file.absolutePath)
    }

    override fun onBitmapSaveError(file: File) {
        endLoading()
        showShortToast(getString(R.string.photopicker_save_failed))
    }

    private fun onResult(path: String?) {
        val intent = Intent()
        intent.putExtra("crop_image", path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}