package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.NewsDetailPresenter
import com.uroad.dubai.api.view.NewsDetailView
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.model.NewsMDL
import com.uroad.glidev4.GlideV4
import kotlinx.android.synthetic.main.activity_details.*

class NewsDetailsActivity : BasePresenterActivity<NewsDetailPresenter>(), NewsDetailView {

    private var newsId: String? = null

    override fun createPresenter(): NewsDetailPresenter = NewsDetailPresenter(this)
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_details)
        newsId = intent.extras?.getString("newsId")
        withTitle(intent.extras?.getString("title")?:getString(R.string.details))
    }

    override fun initData() {
        onPageLoading()
        presenter.getNewsDetail(newsId)
    }

    override fun onGetNews(newsMDL: NewsMDL) {
        onPageResponse()
        updateUI(newsMDL)
    }

    private fun updateUI(newsMDL: NewsMDL) {
        tvDetailTitle.text = newsMDL.title
        tvDetailTime.text = newsMDL.publishtime
        tvDetailContent.text = newsMDL.content
        GlideV4.displayImage(this, newsMDL.headimg, ivDetails, R.color.color_f7)
    }

    override fun onParseError() {
        showShortToast("Parsing error")
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        onPageError()
    }
}