package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adapter.MainBannerAdapter
import com.uroad.dubai.api.presenter.MainBannerPresenter
import com.uroad.dubai.api.view.MainBannerView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.banner.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_mainbanner.*

class MainBannerFragment : BasePresenterFragment<MainBannerPresenter>(), MainBannerView {
    private val handler = Handler()
    override fun createPresenter(): MainBannerPresenter = MainBannerPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainbanner)
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        galleryRv.isNestedScrollingEnabled = false
        galleryRv.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
    }

    override fun initData() {
        presenter.getBanner()
    }

    override fun onGetBanner(list: MutableList<ScenicMDL>) {
        initBanner(list)
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    private fun initBanner(list: MutableList<ScenicMDL>) {
        galleryRv.adapter = MainBannerAdapter(context, list)
        galleryRv.initFlingSpeed(9000).initPageParams(6, 0)
                .initPosition(0)
                .setAnimFactor(0.1f).autoPlay(true)
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val realPos = position % list.size
                        if (realPos in 0 until list.size) {
                            DubaiApplication.clickItemScenic = list[realPos]
                            openActivity(ScenicDetailActivity::class.java)
                        }
                    }
                })
                .intervalTime(5000).setUp()
    }

    override fun onDestroyView() {
        galleryRv.release()
        super.onDestroyView()
    }
}