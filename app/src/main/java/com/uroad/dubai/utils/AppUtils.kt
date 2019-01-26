package com.uroad.dubai.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.uroad.dubai.BuildConfig
import java.io.File
import java.text.DecimalFormat

class AppUtils {
    companion object {
        /*版本更新 apk存放的目录  放到cacheDir文件夹6.0以下无法调起安装（出现解析错误，解析程序包时出现问题）*/
        fun getApkPath(context: Context): String {
            val path: String = try {
                "${context.externalCacheDir?.absolutePath}${File.separator}apk"
            } catch (e: Exception) {
                "${context.getExternalFilesDir(null)?.absolutePath}${File.separator}apk"
            }
            val file = File(path).apply { if (!exists()) this.mkdirs() }
            return file.absolutePath
        }

        fun getFileName(url: String?): String {
            var fileName = ""
            if (url != null && url.lastIndexOf("/") > 0) {
                fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
            }
            return fileName
        }

        fun getReadableFileSize(size: Long): String {
            if (size <= 0) return "0B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
            return DecimalFormat("#.#").format(size / Math.pow(1024.toDouble(), digitGroups.toDouble())) + " " + units[digitGroups]
        }

        fun installApk(context: Context, file: File) {
            try {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = Intent.ACTION_VIEW
                val uri: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context.applicationContext, BuildConfig.APPLICATION_ID + ".provider", file)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    uri = Uri.fromFile(file)
                }
                val type = "application/vnd.android.package-archive"
                intent.setDataAndType(uri, type)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}