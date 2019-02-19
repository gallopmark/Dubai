package com.uroad.dubai.photopicker.data

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.View
import com.uroad.dubai.BuildConfig
import com.uroad.dubai.R
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.photopicker.adapter.ImageGridAdapter
import com.uroad.dubai.photopicker.model.ImageItem
import com.uroad.dubai.photopicker.ui.ImageCropActivity
import com.uroad.dubai.photopicker.ui.ImagePreViewActivity
import com.uroad.dubai.photopicker.ui.PhotoPickerActivity
import com.uroad.dubai.photopicker.utils.ImagePicker
import com.uroad.dubai.common.BaseActivity
import com.uroad.library.compat.AppDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ImagePickerPresenter(private val context: PhotoPickerActivity,
                           private val pickerView: ImagePickerView)
    : BasePresenter<ImagePickerView>(pickerView) {
    private var bundle: Bundle? = null
    private var cameraPath: String? = null
    var isMultiple: Boolean = false
    var limit: Int = 1
    private var isCompress = false //是否需要对图片压缩处理（非剪裁图片的情况下）
    var isCrop = false    //是否需要裁剪
    private var isSaveRectangle = true  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private var outPutX = 800           //裁剪保存宽度
    private var outPutY = 800           //裁剪保存高度
    private var focusWidth = 280         //焦点框的宽度
    private var focusHeight = 280        //焦点框的高度

    companion object {
        private const val REQUEST_CAMERA = 111
        private const val REQUEST_PREVIEW = 112
        private const val REQUEST_CROP = 113
        private const val PERMISSION_SDCARD = 123
        private const val PERMISSION_CAMERA = 456
    }

    fun initBundle(bundle: Bundle?) {
        this.bundle = bundle
        bundle?.let {
            isMultiple = it.getBoolean("isMultiple", false)
            limit = it.getInt("limit", 1)
            isCompress = it.getBoolean("isCompress", false)
            isCrop = it.getBoolean("isCrop", false)
            outPutX = it.getInt("outPutX", outPutX)
            outPutY = it.getInt("outPutY", outPutY)
            focusWidth = it.getInt("focusWidth", focusWidth)
            focusHeight = it.getInt("focusHeight", focusHeight)
            isSaveRectangle = it.getBoolean("isSaveRectangle", false)
        }
    }

    fun getImageFolders() {
        addDisposable(Observable.fromCallable { ImageSource.getImageFolders(context) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pickerView.onGetImageFolders(it) }, {
                    pickerView.onHideLoading()
                    pickerView.onFailure(context.getString(R.string.photopicker_load_error))
                }, { pickerView.onHideLoading() }, { pickerView.onShowLoading() }))
    }

    fun takePhoto() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.resolveActivity(context.packageManager)?.let {
                var takeImageFile = File(getExternalStorageDir())
                takeImageFile = createFile(takeImageFile, "IMG_", ".jpg")
                val imageUri: Uri
                val authority = BuildConfig.APPLICATION_ID + ".provider"
                imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FileProvider.getUriForFile(context, authority, takeImageFile)//通过FileProvider创建一个content类型的Uri
                } else {
                    Uri.fromFile(takeImageFile)
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                context.openActivityForResult(intent, REQUEST_CAMERA)
                cameraPath = takeImageFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getExternalStorageDir(): String {
        val path = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            Environment.getExternalStorageDirectory().absolutePath
        } else Environment.getRootDirectory().absolutePath
        return "$path${File.separator}${context.getString(R.string.dubai)}"
    }

    private fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) folder.mkdirs()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }

    fun hasExternalPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun applyExternalPermission() {
        ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_SDCARD)
    }

    fun hasCameraPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun applyCameraPermission() {
        ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.CAMERA), PERMISSION_CAMERA)
    }

    fun getImageGridAdapter(imageItems: MutableList<ImageItem>): ImageGridAdapter {
        return ImageGridAdapter(context, imageItems, isMultiple, limit).apply {
            setOnImageItemClickListener(object : ImageGridAdapter.OnImageItemClickListener {
                override fun onCamera() {
                    if (hasCameraPermission()) takePhoto()
                    else {
                        applyCameraPermission()
                    }
                }

                override fun onOverSelected(limit: Int) {
                    context.showLongToast(String.format(context.getString(R.string.photopicker_over_limit, limit)))
                }

                override fun onSelected(mDatas: ArrayList<ImageItem>) {
                    if (isMultiple) {
                        pickerView.onImageSelected(mDatas)
                    } else {
                        if (isCrop) {
                            onCrop(mDatas[0].path)
                        } else {
                            onResult(mDatas)
                        }
                    }
                }
            })
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_SDCARD -> {
                var allGranted = true
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                        break
                    }
                }
                if (allGranted) {
                    pickerView.onPermissionGranted()
                } else {
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                            afterDenied(1)
                            break
                        } else {
                            afterDenied(2)
                            break
                        }
                    }
                }
            }
            PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    if (permissions.isNotEmpty() && !ActivityCompat.shouldShowRequestPermissionRationale(context, permissions[0])) {
                        dismissCamera(1)
                    } else {
                        dismissCamera(2)
                    }
                }
            }
        }
    }

    private fun afterDenied(type: Int) {
        var isOpen = false
        val dialog = AppDialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setTitle(context.getString(R.string.dialog_default_title))
        val message = if (type == 1) context.getString(R.string.photopicker_noSdcard) else context.getString(R.string.photopicker_dismiss_sdcard)
        dialog.setMessage(message)
        val positive = if (type == 1) context.getString(R.string.dialog_button_confirm) else context.getString(R.string.openSettings)
        dialog.setNegativeButton(context.getString(R.string.dialog_button_cancel), object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                dialog.dismiss()
            }
        })
        dialog.setPositiveButton(positive, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                isOpen = true
                context.openSettings()
                dialog.dismiss()
            }
        })
        dialog.setOnDismissListener { if (!isOpen) context.finish() }
        dialog.show()
    }

    //用户点击禁止相机或点击不再提示
    private fun dismissCamera(type: Int) {
        when (type) {
            1 -> {
                context.showDialog(context.getString(R.string.dialog_default_title), context.getString(R.string.photopicker_noCamera), object : BaseActivity.DialogViewClickListener {
                    override fun onConfirm(v: View, dialog: AppDialog) {
                        context.openSettings()
                        dialog.dismiss()
                    }

                    override fun onCancel(v: View, dialog: AppDialog) {
                        dialog.dismiss()
                    }
                })
            }
            else -> {
                context.showDialog(context.getString(R.string.dialog_default_title), context.getString(R.string.photopicker_dismiss_camera), object : BaseActivity.DialogViewClickListener {
                    override fun onConfirm(v: View, dialog: AppDialog) {
                        applyCameraPermission()
                        dialog.dismiss()
                    }

                    override fun onCancel(v: View, dialog: AppDialog) {
                        dialog.dismiss()
                    }
                })
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    cameraPath?.let {
                        if (File(it).exists()) {
                            if (isCrop) {
                                onCrop(it)
                            } else {
                                onResult(ArrayList<ImageItem>().apply { add(ImageItem().apply { path = it }) })
                            }
                        }
                    }
                }
            }
            REQUEST_PREVIEW -> {
                val images = data?.getParcelableArrayListExtra<ImageItem>("images")
                pickerView.onPreviewCallback(images, resultCode)
            }
            REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    onResult(ArrayList<ImageItem>().apply { add(ImageItem().apply { path = data?.getStringExtra("crop_image") }) })
                }
            }
        }
    }

    fun onCrop(cropPath: String?) {
        val bundle = this.bundle?.apply { putString("cropPath", cropPath) }
        context.openActivityForResult(ImageCropActivity::class.java, bundle, REQUEST_CROP)
    }

    fun onPreview(data: ArrayList<ImageItem>) {
        val bundle = Bundle().apply { putParcelableArrayList("photos", data) }
        context.openActivityForResult(ImagePreViewActivity::class.java, bundle, REQUEST_PREVIEW)
    }

    fun onResult(photos: MutableList<ImageItem>) {
        if (!isCrop && isCompress) {   //非剪裁情况下，对图片压缩
            addDisposable(Observable.fromArray(photos).map { items -> onCompress(items) }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        context.endLoading()
                        val paths = ArrayList<String>()
                        for (item in it) paths.add(item.absolutePath)
                        setResult(paths)
                    }, {
                        context.endLoading()
                        context.showShortToast(context.getString(R.string.photopicker_luban_error))
                    }, { context.endLoading() }, { context.showLoading() }))
        } else {
            setResult(convertStringPaths(photos))
        }
    }

    private fun convertStringPaths(items: MutableList<ImageItem>): ArrayList<String> {
        return ArrayList<String>().apply { for (item in items) item.path?.let { add(it) } }
    }

    private fun onCompress(items: MutableList<ImageItem>): MutableList<File> {
        val paths = convertStringPaths(items)
        return if (File(DubaiApplication.COMPRESSOR_PATH).exists())
            Luban.with(context).setTargetDir(DubaiApplication.COMPRESSOR_PATH).load(paths).get()
        else
            Luban.with(context).load(paths).get()
    }

    private fun setResult(paths: ArrayList<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra(ImagePicker.EXTRA_PATHS, paths)
        context.setResult(Activity.RESULT_OK, intent)
        context.finish()
    }
}