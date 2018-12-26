package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.MessagesMDL

interface MessagesView : BaseView {
    fun onGetNewList(list: MutableList<MessagesMDL>)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}
