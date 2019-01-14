package com.uroad.dubai.photopicker.data

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.uroad.dubai.R
import com.uroad.dubai.photopicker.model.ImageFolder
import com.uroad.dubai.photopicker.model.ImageItem
import java.io.File

class ImageSource {
    companion object {
        private val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        private val projection = arrayOf(//查询图片需要的数据列
                MediaStore.Images.Media.DISPLAY_NAME, //图片的显示名称
                MediaStore.Images.Media.DATA, //图片的真实路径
                MediaStore.Images.Media.SIZE, //图片的大小，long型
                MediaStore.Images.Media.MIME_TYPE, //图片的类型     image/jpeg
                MediaStore.Images.Media.DATE_ADDED)    //图片被添加的时间，long型

        /*获取最近100张图片*/
        private fun getLatelyImages(context: Context): MutableList<ImageItem> {
            val resolver = context.contentResolver
            val cursor = resolver.query(contentUri, projection, null, null, "${projection[4]} DESC LIMIT 0,100")
            val images = ArrayList<ImageItem>()
            cursor?.let { while (it.moveToNext()) images.add(assignItem(it)) }
            cursor?.close()
            return images
        }

        private fun assignItem(cursor: Cursor): ImageItem {
            return ImageItem().apply {
                this.name = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]))
                this.path = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                this.size = cursor.getLong(cursor.getColumnIndexOrThrow(projection[2]))
                this.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(projection[3]))
                this.addTime = cursor.getLong(cursor.getColumnIndexOrThrow(projection[4]))
            }
        }

        /*获取所有图片*/
        private fun getAllPictures(context: Context): MutableList<ImageItem> {
            val resolver = context.contentResolver
            val cursor = resolver.query(contentUri, projection, null, null, "${projection[4]} DESC")
            val images = ArrayList<ImageItem>()
            cursor?.let { while (it.moveToNext()) images.add(assignItem(it)) }
            cursor?.close()
            return images
        }

        fun getImageFolders(context: Context): MutableList<ImageFolder> {
            val latelyData = getLatelyImages(context)
            val folders = ArrayList<ImageFolder>()
            folders.add(ImageFolder().apply {
                mediaItems.addAll(latelyData)
                firstImagePath = if (mediaItems.size > 0) mediaItems[0].path else null
                name = context.resources.getString(R.string.photopicker_recent_pictures)
            })
            val data = getAllPictures(context)
            for (i in 0 until data.size) {
                val item = data[i]
                val parent = File(item.path).parentFile
                val folder = ImageFolder(parent.absolutePath, parent.name)
                if (!folders.contains(folder)) {
                    folder.firstImagePath = item.path
                    folder.mediaItems.add(item)
                    folders.add(folder)
                } else {
                    folders[folders.indexOf(folder)].mediaItems.add(item)
                }
            }
            return folders
        }
    }
}