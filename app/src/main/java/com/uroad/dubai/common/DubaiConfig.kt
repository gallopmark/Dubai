package com.uroad.dubai.common

class DubaiConfig {
    companion object {
        var isDebug = true   //debug模式 异常不上传到bugly
        var isApiDebug = true   //是否是正式环境开关
    }
}