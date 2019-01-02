package com.uroad.dubai.api

import com.uroad.dubai.webService.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

open class BasePresenter<V : BaseView>(private var baseView: V?) {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    /**
     * 解除绑定
     */
    fun detachView() {
        baseView = null
        dispose()
    }

    fun getBaseV() = baseView

    fun request(method: String?, params: HashMap<String, String?>, observer: StringObserver) {
        val disposable = ApiService.doRequest(method, params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer)
        compositeDisposable.add(disposable)
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