package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.NoticeMDL

interface NoticeView : BaseView {
    fun getNoticeList(data: MutableList<NoticeMDL>)
    fun onFailure(errorMsg: String?, errorCode: Int?)
}