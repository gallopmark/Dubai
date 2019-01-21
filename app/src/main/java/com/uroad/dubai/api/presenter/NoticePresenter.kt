package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.NoticeView
import com.uroad.dubai.model.NoticeMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class NoticePresenter(private val noticeView: NoticeView)
    : BasePresenter<NoticeView>(noticeView) {

    fun getNotices() {
        request(WebApi.GET_NOTICE, WebApi.getBaseParams(), object : StringObserver(noticeView) {
            override fun onHttpResultOk(data: String?) {
                noticeView.getNoticeList(GsonUtils.fromDataToList(data, NoticeMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                noticeView.onFailure(errorMsg, errorCode)
            }
        })
    }
}