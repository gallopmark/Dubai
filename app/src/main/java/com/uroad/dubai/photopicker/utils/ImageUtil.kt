package com.uroad.dubai.photopicker.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException

class ImageUtil private constructor() {
    companion object {
        fun zoomBitmap(bm: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
            // 获得图片的宽高
            val width = bm.width
            val height = bm.height
            // 计算缩放比例
            val scaleWidth = reqWidth.toFloat() / width
            val scaleHeight = reqHeight.toFloat() / height
            val scale = Math.min(scaleWidth, scaleHeight)
            // 取得想要缩放的matrix参数
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            // 得到新的图片
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
        }

        /**
         * 根据计算的inSampleSize，得到压缩后图片
         * @param pathName
         * @param reqWidth
         * @param reqHeight
         * @return
         */
        fun decodeSampledBitmapFromFile(pathName: String, reqWidth: Int, reqHeight: Int): Bitmap? {
            var degree = 0
            try {
                val exifInterface = ExifInterface(pathName)
                val result = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                when (result) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            } catch (e: IOException) {
                return null
            }
            try {
                // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(pathName, options)
                // 调用上面定义的方法计算inSampleSize值
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
                // 使用获取到的inSampleSize值再次解析图片
                options.inJustDecodeBounds = false
                //            options.inPreferredConfig = Bitmap.Config.RGB_565;
                val bitmap: Bitmap = BitmapFactory.decodeFile(pathName, options)
                if (degree != 0) {
                    val newBitmap = rotateImageView(bitmap, degree)
                    bitmap.recycle()
                    return newBitmap
                }

                return bitmap
            } catch (error: OutOfMemoryError) {
                return null
            }
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // 源图片的宽度
            val width = options.outWidth
            val height = options.outHeight
            var inSampleSize = 1
            if (width > reqWidth && height > reqHeight) {
                //         计算出实际宽度和目标宽度的比率
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                inSampleSize = Math.max(widthRatio, heightRatio)
            }
            return inSampleSize
        }

        fun rotateImageView(bitmap: Bitmap, angle: Int): Bitmap {
            //旋转图片 动作
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            // 创建新的图片
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }
}