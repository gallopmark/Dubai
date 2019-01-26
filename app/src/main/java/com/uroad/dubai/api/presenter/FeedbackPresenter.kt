package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.FeedbackView
import org.json.JSONObject

class FeedbackPresenter(val feedbackView: FeedbackView) : BasePresenter<FeedbackView>(feedbackView) {
    fun post(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(feedbackView) {
            override fun onHttpResultOk(data: String?) {
                feedbackView.onPostSuccess(JSONObject(data).getString("data").toString())
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                feedbackView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}