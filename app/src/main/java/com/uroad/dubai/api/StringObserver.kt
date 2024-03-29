package com.uroad.dubai.api

import com.uroad.dubai.utils.AndroidBase64Utils
import com.uroad.dubai.utils.GsonUtils

abstract class StringObserver() : BaseObserver<String>(){

    constructor(view: BaseView?) : this(){
        this.view = view
    }

    override fun onSuccess(result: String) {
        val data = AndroidBase64Utils.decodeToString(result)
        if (GsonUtils.isResultOk(data)) {
            onHttpResultOk(data)
        } else {
            onHttpResultError(GsonUtils.getMsg(data), GsonUtils.getCode(data))
        }
    }

    abstract fun onHttpResultOk(data: String?)
    abstract fun onHttpResultError(errorMsg: String?, errorCode: Int?)
    override fun onFailure(msg: String?) {
    }
}