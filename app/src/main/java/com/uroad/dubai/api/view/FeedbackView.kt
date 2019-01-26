package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView

interface FeedbackView : BaseView {
    fun onPostSuccess(msg : String)
    fun onHttpResultError(errorMsg: String?, errorCode: Int?)
}