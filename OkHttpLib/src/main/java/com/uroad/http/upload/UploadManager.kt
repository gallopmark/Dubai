package com.uroad.http.upload

import com.uroad.http.util.ConstUtils
import com.uroad.http.util.MultipartUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.http.Url
import java.io.File


class UploadManager {
    companion object {
        fun uploadFile(url: String, fileKey: String?, file: File): Disposable {
            return uploadFile(url, fileKey, file, null)
        }

        fun uploadFile(url: String, fileKey: String?, file: File, listener: SingleFileUploadListener?): Disposable {
            return transform(ConstUtils.buildManager().createApi(UploadApi::class.java)
                    .uploadFileWithPart(url, UploadApi.fileToMultipartBodyPart(fileKey, file, listener)), listener)
        }

        fun uploadFilesWithParts(url: String, fileKey: String?, files: MutableList<File>): Disposable {
            return uploadFilesWithParts(url, fileKey, files, null)
        }

        fun uploadFilesWithParts(url: String, fileKey: String?, files: MutableList<File>, listener: FileUploadListener?): Disposable {
            return transform(ConstUtils.buildManager().createApi(UploadApi::class.java)
                    .uploadFilesWithParts(url, MultipartUtils.filesToMultipartBodyParts(fileKey, files)), listener)
        }

        fun uploadFileWithRequestBody(url: String, fileKey: String?, files: MutableList<File>, listener: FileUploadListener?): Disposable {
            return transform(ConstUtils.buildManager().createApi(UploadApi::class.java)
                    .uploadFileWithRequestBody(url, MultipartUtils.filesToMultipartBody(fileKey, files)), listener)
        }

        private fun transform(observable: Observable<ResponseBody>, listener: FileUploadListener?): Disposable {
            return observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ listener?.onSuccess(it) }, { listener?.onFailure(it) }, {}, { listener?.onStart(it) })
        }
    }
}