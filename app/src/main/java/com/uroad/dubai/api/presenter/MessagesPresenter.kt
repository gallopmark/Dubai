package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.view.MessagesView
import com.uroad.dubai.webService.WebApi

class MessagesPresenter(val messagesView: MessagesView) : BasePresenter<MessagesView>(messagesView) {
    fun messageCenter(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(messagesView) {
            override fun onHttpResultOk(data: String?) {
                messagesView.onGetNewList(GsonUtils.fromDataToList(data, MessagesMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                messagesView.onShowError(errorMsg)
            }
        })
    }

    fun messageDetail(messageId: String?, detailView: MessageDetailView) {
        request(WebApi.MESSAGE_DETAIL, WebApi.messageDetailParams(messageId), object : StringObserver(detailView) {
            override fun onHttpResultOk(data: String?) {
                detailView.onSuccess(GsonUtils.getDataAsString(data))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                detailView.onFailure(errorMsg)
            }
        })
    }

    interface MessageDetailView : BaseView {
        fun onSuccess(data: String?)
        fun onFailure(errorMsg: String?)
    }
}