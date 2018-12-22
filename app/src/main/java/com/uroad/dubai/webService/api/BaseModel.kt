package com.uroad.dubai.webService.api

class BaseModel<T> {
    var status: Boolean? = false    //接口状态	false异常 true正常
    var data: T? = null    //返回业务内容（JSON）	status 为 true 时才有
    var msg: String? = null    //返回的提示信息
    var code: String? = null //	状态码
}