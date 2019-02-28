package com.uroad.http.download

import com.uroad.http.util.ConstUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileDownloader {

    companion object {

        fun download(@NotNull url: String, @NotNull targetPath: String): Disposable {
            return download(url, targetPath, null)
        }

        fun download(@NotNull url: String, @NotNull targetPath: String, listener: FileDownloadListener?): Disposable {
            return ConstUtils.buildManager().createApi(DownloadApi::class.java).downloadFile(url)
                    .map { saveFile(it, url, targetPath, listener) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ listener?.onDownloadCompleted(it) }, { listener?.onError(it) }, {}, { listener?.onStart(it) })
        }

        @Throws(IOException::class)
        private fun saveFile(response: ResponseBody, url: String, targetPath: String, downloadListener: FileDownloadListener?): String {
            val contentLength = response.contentLength()
            var `is`: InputStream? = null
            val buf = ByteArray(2048)
            var fos: FileOutputStream? = null
            try {
                `is` = response.byteStream()
                var sum: Long = 0
                val dir = File(targetPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val destFileName = getFileNameFromUrl(url)
                val file = File(dir, destFileName)
                fos = FileOutputStream(file)
                while (`is`.read(buf) != -1) {
                    sum += `is`.read(buf)
                    fos.write(buf, 0, `is`.read(buf))
                    val finalSum = sum
                    downloadListener?.onProgress(finalSum, contentLength, (finalSum * 1.0f / contentLength * 100))
                }
                fos.flush()
                return file.absolutePath
            } finally {
                try {
                    response.close()
                    `is`?.close()
                } catch (e: IOException) {
                }
                try {
                    fos?.close()
                } catch (e: IOException) {
                }
            }
        }

        private fun getFileNameFromUrl(url: String?): String {
            if (!url.isNullOrEmpty() && url.lastIndexOf("/") > 0) {
                val lastIndex = url.lastIndexOf("/") + 1
                return url.substring(lastIndex, url.length)
            }
            return ""
        }
    }
}