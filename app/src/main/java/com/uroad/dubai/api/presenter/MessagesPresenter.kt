package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.view.MessagesView

class MessagesPresenter(val messagesView: MessagesView) : BasePresenter<MessagesView>(messagesView) {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(messagesView) {
            override fun onHttpResultOk(data: String?) {
                messagesView.onGetNewList(GsonUtils.fromDataToList(data, MessagesMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                messagesView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}