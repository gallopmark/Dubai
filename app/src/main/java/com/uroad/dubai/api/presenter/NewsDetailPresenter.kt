package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.NewsDetailView
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class NewsDetailPresenter(private val newsDetailView: NewsDetailView)
    : BasePresenter<NewsDetailView>(newsDetailView) {

    fun getNewsDetail(newsId: String?) {
        request(WebApi.GET_NEWS_DETAIL, WebApi.newsDetailParams(newsId), object : StringObserver(newsDetailView) {
            override fun onHttpResultOk(data: String?) {
                val mdl = GsonUtils.fromDataBean(data, NewsMDL::class.java)
                if (mdl == null) newsDetailView.onParseError()
                else newsDetailView.onGetNews(mdl)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                newsDetailView.onFailure(errorMsg, errorCode)
            }
        })
    }
}