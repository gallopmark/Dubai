package com.uroad.dubai.api

interface BaseView {

    fun onShowLoading()

    fun onHideLoading()
    /**
     * 显示错误信息
     *
     * @param msg
     */
    fun onShowError(msg: String?)
}