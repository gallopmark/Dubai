package com.uroad.dubai.webService.api

import io.reactivex.observers.DisposableObserver
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import org.json.JSONException
import retrofit2.HttpException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLHandshakeException


abstract class BaseObserver<T>(var view: BaseView?) : DisposableObserver<T>() {

    override fun onStart() {
        view?.onShowLoading()
    }

    override fun onNext(result: T) {
        onSuccess(result)
    }

    override fun onError(e: Throwable) {
        view?.onHideLoading()
        val message: String?
        if (e is HttpException) {
            message = try {
                val errorBody = e.response().errorBody()
                if (errorBody != null) errorBody.string() else "未知错误"
            } catch (e1: Exception) {
                e1.message
            }
        } else if (e is SocketTimeoutException) {
            message = "网络连接超时，请检查您的网络状态，稍后重试！"
        } else if (e is ConnectException) {
            message = "网络连接异常，请检查您的网络状态，稍后重试！"
        } else if (e is TimeoutException) {
            message = "网络连接超时，请检查您的网络状态，稍后重试！"
        } else if (e is UnknownHostException) {
           message = "网络连接异常，请检查您的网络状态，稍后重试！"
        } else if (e is NullPointerException) {
            message = "空指针异常"
        } else if (e is SSLHandshakeException) {
            message = "证书验证失败"
        } else if (e is ClassCastException) {
            message = "类型转换错误"
        } else if (e is JsonParseException
                || e is JSONException
                || e is JsonSerializer<*>
                || e is NotSerializableException
                || e is java.text.ParseException) {
            message = "解析错误"
        } else if (e is IllegalStateException) {
            message = "非法数据异常"
        } else {
            message = "未知错误"
        }
        view?.onShowError(message)
        onFailure(message)
    }

    override fun onComplete() {
        view?.onHideLoading()
    }

    abstract fun onSuccess(result: T)

    abstract fun onFailure(msg: String?)
}