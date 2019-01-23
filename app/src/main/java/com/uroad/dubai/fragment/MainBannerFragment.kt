package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.activity.NewsDetailsActivity
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adaptervp.MainBannerAdapter
import com.uroad.dubai.api.presenter.BannerPresenter
import com.uroad.dubai.api.view.BannerView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.BannerType
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.library.widget.banner.BaseBannerAdapter
import kotlinx.android.synthetic.main.fragment_mainbanner.*

class MainBannerFragment : BasePresenterFragment<BannerPresenter>(), BannerView {

    private val handler = Handler()
    private lateinit var data: MutableList<NewsMDL>
    private lateinit var adapter: MainBannerAdapter

    override fun createPresenter(): BannerPresenter = BannerPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainbanner)
        initDataView()
    }

    private fun initDataView() {
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        data = ArrayList()
        adapter = MainBannerAdapter(context, data).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<NewsMDL> {
                override fun onItemClick(t: NewsMDL, position: Int) {
                    if (TextUtils.equals(t.newstype, NewsType.HOTEL.code) ||
                            TextUtils.equals(t.newstype, NewsType.RESTAURANT.code) ||
                            TextUtils.equals(t.newstype, NewsType.ATTRACTION.code)) {
                        openActivity(ScenicDetailActivity::class.java, Bundle().apply { putString("newsId", t.newsid) })
                    } else if (TextUtils.equals(t.newstype, NewsType.NEWS.code)) {
                        openActivity(NewsDetailsActivity::class.java, Bundle().apply { putString("newsId", t.newsid) })
                    }
//                    DubaiApplication.clickItemScenic = t
//                    openActivity(ScenicDetailActivity::class.java)
                }
            })
        }
        banner.setAdapter(adapter)
    }

    override fun initData() {
        presenter.getBannerNews(BannerType.HOME.CODE)
    }

    override fun onGetNews(data: MutableList<NewsMDL>) {
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }
}