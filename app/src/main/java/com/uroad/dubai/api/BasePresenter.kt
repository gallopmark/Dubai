package com.uroad.dubai.api

import com.uroad.dubai.webService.ApiService
import com.uroad.dubai.api.upload.RequestBodyWrapper
import com.uroad.dubai.api.upload.UploadFileCallback
import com.uroad.zhgs.utils.MimeTypeTool
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    private fun contentTypeFor(file: File): String = MimeTypeTool.getMimeType(file)

    open fun doUpload(filePath: String?, callback: UploadFileCallback?) {
        doUpload(File(filePath), callback)
    }

    open fun doUpload(filePath: String?, fileKey: String?, callback: UploadFileCallback?) {
        doUpload(File(filePath), fileKey, callback)
    }

    open fun doUpload(file: File, callback: UploadFileCallback?) {
        doUpload(file, "file", callback)
    }

    open fun doUpload(file: File, fileKey: String?, callback: UploadFileCallback?) {
        val requestBody = RequestBody.create(MediaType.parse(contentTypeFor(file)), file)
        val wrapper = RequestBodyWrapper(requestBody, callback)
        val key = fileKey ?: "file"
        val part = MultipartBody.Part.createFormData(key, file.name, wrapper)
        val observable = ApiRetrofit.createApi(ApiService::class.java).uploadFile(part)
        val disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val json = it?.string()
                    if (json != null) {
                        callback?.onSuccess(json)
                    } else {
                        callback?.onFailure(Throwable())
                    }
                }, {
                    callback?.onFailure(it)
                }, {
                    callback?.onComplete()
                }, {
                    callback?.onStart(it)
                })
        addDisposable(disposable)
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