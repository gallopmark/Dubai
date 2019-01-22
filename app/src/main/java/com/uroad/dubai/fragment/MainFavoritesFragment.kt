package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.adaptervp.MainSubscribeAdapter
import com.uroad.dubai.api.presenter.SubscribePresenter
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.SubscribeMDL
import kotlinx.android.synthetic.main.fragment_mainfavorites.*

/**
 * @author MFB
 * @create 2019/1/22
 * @describe main page favorites
 */
class MainFavoritesFragment : BasePresenterFragment<SubscribePresenter>(), SubscribeView {

    private lateinit var data: MutableList<SubscribeMDL>
    private lateinit var adapter: MainSubscribeAdapter
    private lateinit var handler: Handler
    private var isAutomatic = false
    override fun createPresenter(): SubscribePresenter = SubscribePresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainfavorites)
        initViewParams()
        initial()
        handler = Handler()
    }

    private fun initViewParams() {
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun initial() {
        data = ArrayList()
        adapter = MainSubscribeAdapter(context, data)
        bannerView.setAdapter(adapter)
    }

    override fun initData() {
        presenter.getSubscribeData(getAndroidID())
    }

    override fun onGetSubscribeData(data: MutableList<SubscribeMDL>) {
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }
}