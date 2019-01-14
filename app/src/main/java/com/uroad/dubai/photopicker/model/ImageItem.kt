package com.uroad.dubai.photopicker.model

import android.os.Parcel
import android.os.Parcelable

class ImageItem : Parcelable {
    var name: String? = null       //图片的名字
    var path: String? = null       //图片的路径
    var mimeType: String? = null   //图片的类型
    var size: Long = 0    //图片大小
    var addTime: Long = 0      //图片的创建时间
    var showCamera: Boolean = false

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<ImageItem> = object : Parcelable.Creator<ImageItem> {
            override fun createFromParcel(source: Parcel): ImageItem {
                return ImageItem(source)
            }

            override fun newArray(size: Int): Array<ImageItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor()
    constructor(parcel: Parcel) {
        name = parcel.readString()
        path = parcel.readString()
        mimeType = parcel.readString()
        size = parcel.readLong()
        addTime = parcel.readLong()
    }

    /* 图片的路径和创建时间相同就认为是同一张图片*/
    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is ImageItem -> false
            else -> this === other || path == other.path && name == other.name
        }
    }

    override fun hashCode(): Int {
        return 31 + (path?.hashCode() ?: 0)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeString(mimeType)
        dest.writeLong(size)
        dest.writeLong(addTime)
    }

    override fun describeContents(): Int {
        return 0
    }
}