package com.uroad.library.data

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.BigDecimal

/*app文件管理*/
class AppDataManager {
    companion object {
        fun getAppCacheSize(context: Context): Long {
            var cacheSize = getFolderSize(context.cacheDir)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                cacheSize += getFolderSize(context.externalCacheDir)
            }
            return cacheSize
        }

        /**
         * 获取缓存大小
         */
        fun getTotalCacheSize(context: Context): String {
            var cacheSize = getFolderSize(context.cacheDir)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                cacheSize += getFolderSize(context.externalCacheDir)
            }
            return getFormatSize(cacheSize.toDouble())
        }

        /**
         * 清除缓存
         */
        fun clearAllCache(context: Context) {
            deleteDir(context.cacheDir)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                deleteDir(context.externalCacheDir)
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            if (dir == null) return true
            if (dir.isDirectory) {
                val children = dir.list()
                for (aChildren in children) {
                    val success = deleteDir(File(dir, aChildren))
                    if (!success) {
                        return false
                    }
                }
            }
            return dir.delete()
        }

        // 获取文件大小
        //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
        //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
        fun getFolderSize(file: File?): Long {
            var size: Long = 0
            try {
                val fileList = file!!.listFiles()
                for (aFileList in fileList) {
                    // 如果下面还有文件
                    size = if (aFileList.isDirectory) {
                        size + getFolderSize(aFileList)
                    } else {
                        size + aFileList.length()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return size
        }

        /**
         * 格式化单位
         */
        fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            //        if (kiloByte < 1) {
            //            return "0K";
            //        }
            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                return BigDecimal(kiloByte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
            }
            val gigabyte = megaByte / 1024
            if (gigabyte < 1) {
                return BigDecimal(megaByte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M"
            }
            val teraBytes = gigabyte / 1024
            if (teraBytes < 1) {
                return BigDecimal(gigabyte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
            }
            return BigDecimal(teraBytes).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }
    }
}