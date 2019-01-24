package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.UserMDL

interface LoginView : BaseView {
    fun loginSuccess( user : UserMDL?)
    fun loginError(e:String)
    //fun onGetNewList(news:MutableList<CalendarMDL>)
    //fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}