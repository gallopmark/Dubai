package com.uroad.dubai.photopicker.data

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.photopicker.model.ImageFolder
import com.uroad.dubai.photopicker.model.ImageItem

interface ImagePickerView : BaseView {
    fun onPermissionGranted()
    fun onGetImageFolders(folders: MutableList<ImageFolder>)
    fun onFailure(errorMsg: String?)
    fun onPreviewCallback(images: ArrayList<ImageItem>?, resultCode: Int)
    fun onImageSelected(images: ArrayList<ImageItem>)
}