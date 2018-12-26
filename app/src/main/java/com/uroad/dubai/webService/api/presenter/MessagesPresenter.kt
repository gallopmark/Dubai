package com.uroad.dubai.webService.api.presenter

import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.api.BasePresenter
import com.uroad.dubai.webService.api.StringObserver
import com.uroad.dubai.webService.api.view.MessagesView
import com.uroad.dubai.webService.api.view.NewsView

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