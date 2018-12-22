package com.uroad.dubai.webService.api.presenter

import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.api.BasePresenter
import com.uroad.dubai.webService.api.StringObserver
import com.uroad.dubai.webService.api.view.NewsView

class NewsPresenter(val newsView: NewsView) : BasePresenter<NewsView>(newsView) {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(newsView) {
            override fun onHttpResultOk(data: String?) {
                newsView.onGetNewList(GsonUtils.fromDataToList(data, NewsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                newsView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}