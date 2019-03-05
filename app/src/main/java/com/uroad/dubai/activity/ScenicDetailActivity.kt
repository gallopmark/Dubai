package com.uroad.dubai.activity

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.NewsDetailPresenter
import com.uroad.dubai.api.view.NewsDetailView
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.NewsMDL
import com.uroad.glidev4.GlideV4
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.utils.PhoneUtils
import kotlinx.android.synthetic.main.activity_scenicdetail.*

class ScenicDetailActivity : BasePresenterActivity<NewsDetailPresenter>(), NewsDetailView {

    private var newsId: String? = null
    private var newsMDL: NewsMDL? = null

    override fun createPresenter(): NewsDetailPresenter = NewsDetailPresenter(this)
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_scenicdetail, true)
        newsId = intent.extras?.getString("newsId")
        initView()
        initLayout()
    }

    private fun initView() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        llTopLayout.setOnClickListener { newsMDL?.let { dataMDL -> openActivity(RoadNavigationActivity::class.java, Bundle().apply { putSerializable("dataMDL", dataMDL) }) } }
//        val behavior = (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppbarLayoutZoomBehavior
//        behavior.setMaxZoomHeight(DisplayUtils.getWindowHeight(this).toFloat())
    }

    private fun initLayout() {
        val statusHeight = DisplayUtils.getStatusHeight(this)
        toolbar.layoutParams = (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply { topMargin = statusHeight }
        val picHeight = DisplayUtils.getWindowHeight(this) / 3
        appbarZoomView.layoutParams = (appbarZoomView.layoutParams as FrameLayout.LayoutParams).apply { height = picHeight }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            collapsingLayout.isTitleEnabled = offset > total / 2
        })
    }

    override fun initData() {
        onPageLoading()
        presenter.getNewsDetail(newsId)
    }

    override fun onGetNews(newsMDL: NewsMDL) {
        onPageResponse()
        updateUI(newsMDL)
    }

    override fun onParseError() {
        showShortToast("Parsing error")
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        onPageError()
    }

    private fun updateUI(mdl: NewsMDL) {
        this.newsMDL = mdl
        GlideV4.displayImage(this, mdl.headimg, appbarZoomView)
        tvSubTitle.text = mdl.title
        tvAddress.text = mdl.address
        tvContent.text = mdl.content
        tvHours.text = mdl.hours
        tvTel.text = mdl.phone
        collapsingLayout.title = mdl.title
        tvTel.setOnClickListener { PhoneUtils.call(this@ScenicDetailActivity, mdl.phone ?: "") }
    }
}