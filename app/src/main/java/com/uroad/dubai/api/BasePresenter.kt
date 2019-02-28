package com.uroad.dubai.api

import com.uroad.dubai.webService.ApiService
import com.uroad.dubai.api.upload.RequestBodyWrapper
import com.uroad.dubai.api.upload.UploadFileCallback
import com.uroad.http.RetrofitManager
import com.uroad.http.upload.FileUploadListener
import com.uroad.http.upload.SingleFileUploadListener
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

abstract class BasePresenter<V : BaseView> {
    private var baseView: V? = null

    constructor()
    constructor(baseView: V?) {
        this.baseView = baseView
    }

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    /**
     * 解除绑定
     */
    open fun detachView() {
        baseView = null
        dispose()
    }

    fun getBaseV() = baseView

    fun request(method: String?, params: HashMap<String, String?>, observer: BaseObserver<String>): Disposable {
        val disposable = ApiService.doRequest(method, params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer)
        compositeDisposable.add(disposable)
        return disposable
    }

    fun request(method: String?, params: HashMap<String, String?>, observer: StringObserver): Disposable {
        val disposable = ApiService.doRequest(method, params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer)
        compositeDisposable.add(disposable)
        return disposable
    }

    open fun doUpload(filePath: String?, callback: SingleFileUploadListener?) {
        doUpload(File(filePath), callback)
    }

    open fun doUpload(filePath: String?, fileKey: String?, callback: SingleFileUploadListener?) {
        doUpload(fileKey, File(filePath), callback)
    }

    open fun doUpload(file: File, callback: SingleFileUploadListener?) {
        doUpload("file", file, callback)
    }

    open fun doUpload(fileKey: String?, file: File, callback: SingleFileUploadListener?) {
        addDisposable(RetrofitManager.uploadFile(ApiService.UPLOAD_URL, fileKey, file, callback))
    }

    fun <T> addDisposable(observable: Observable<T>, observer: BaseObserver<T>) {
        compositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer))
    }

    fun addDisposable(disposable: Disposable?) {
        disposable?.let { compositeDisposable.add(it) }
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}