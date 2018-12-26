package com.uroad.dubai.webService.api.view

import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.webService.api.BaseView

interface MessagesView : BaseView{
    fun onGetNewList(list: MutableList<MessagesMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
