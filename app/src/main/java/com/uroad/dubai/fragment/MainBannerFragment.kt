package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adaptervp.MainBannerAdapter
import com.uroad.dubai.api.presenter.MainBannerPresenter
import com.uroad.dubai.api.view.MainBannerView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.widget.banner.BaseBannerAdapter
import kotlinx.android.synthetic.main.fragment_mainbanner.*

class MainBannerFragment : BasePresenterFragment<MainBannerPresenter>(), MainBannerView {
    private val handler = Handler()
    private lateinit var data: MutableList<ScenicMDL>
    private lateinit var adapter: MainBannerAdapter

    override fun createPresenter(): MainBannerPresenter = MainBannerPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainbanner)
        initDataView()
    }

    private fun initDataView() {
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        data = ArrayList()
        adapter = MainBannerAdapter(context, data).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<ScenicMDL> {
                override fun onItemClick(t: ScenicMDL, position: Int) {
                    DubaiApplication.clickItemScenic = t
                    openActivity(ScenicDetailActivity::class.java)
                }
            })
        }
        banner.setAdapter(adapter)
    }

    override fun initData() {
        presenter.getBanner()
    }

    override fun onGetBanner(list: MutableList<ScenicMDL>) {
        this.data.clear()
        this.data.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }
}