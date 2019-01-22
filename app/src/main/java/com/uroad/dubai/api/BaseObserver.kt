package com.uroad.dubai.api

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
                if (errorBody != null) errorBody.string() else "unknown error"
            } catch (e1: Exception) {
                e1.message
            }
        } else if (e is SocketTimeoutException) {
            message = "Network connection timeout, please check your network status, try again later!"
        } else if (e is ConnectException) {
            message = "Network connection is abnormal. Please check your network status and try again later!"
        } else if (e is TimeoutException) {
            message = "Network connection timeout, please check your network status, try again later!"
        } else if (e is UnknownHostException) {
           message = "Network connection is abnormal. Please check your network status and try again later!"
        } else if (e is NullPointerException) {
            message = "Null pointer exception"
        } else if (e is SSLHandshakeException) {
            message = "Certificate validation failed"
        } else if (e is ClassCastException) {
            message = "Type conversion error"
        } else if (e is JsonParseException
                || e is JSONException
                || e is JsonSerializer<*>
                || e is NotSerializableException
                || e is java.text.ParseException) {
            message = "Parsing error"
        } else if (e is IllegalStateException) {
            message = "Illegal data exception"
        } else {
            message = "unknown error"
        }
        view?.onShowError(message)
        onFailure(message)
    }

    override fun onComplete() {
        view?.onHideLoading()
    }

    abstract fun onSuccess(result: T)

    open fun onFailure(msg: String?){}
}