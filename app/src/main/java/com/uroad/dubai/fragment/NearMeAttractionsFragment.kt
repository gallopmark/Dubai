package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adapter.AttractionListCardAdapter
import com.uroad.dubai.api.presenter.AttractionPresenter
import com.uroad.dubai.api.view.AttractionView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeAttractionsFragment : BasePresenterFragment<AttractionPresenter>(), AttractionView {

    private val data = ArrayList<ScenicMDL>()
    private lateinit var adapter: AttractionListCardAdapter
    private val handler = Handler()

    override fun createPresenter(): AttractionPresenter = AttractionPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = AttractionListCardAdapter(context, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    DubaiApplication.clickItemScenic = data[position]
                    openActivity(ScenicDetailActivity::class.java)
                }
            })
        }
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter.getAttractions(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4))
    }

    override fun onGetAttraction(attractions: MutableList<ScenicMDL>) {
        this.data.clear()
        this.data.addAll(attractions)
        adapter.notifyDataSetChanged()
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}