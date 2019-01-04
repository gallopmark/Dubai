package com.uroad.dubai.fragment

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.adapter.AttractionListAdapter
import com.uroad.dubai.api.presenter.NearMeAttractionPresenter
import com.uroad.dubai.api.view.NearMeAttractionView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeAttractionsFragment : BasePresenterFragment<NearMeAttractionPresenter>(), NearMeAttractionView {

    private val data = ArrayList<ScenicMDL>()
    private lateinit var adapter: AttractionListAdapter
    private val handler = Handler()

    override fun createPresenter(): NearMeAttractionPresenter = NearMeAttractionPresenter(this)

    override fun onPresenterSetUp(view: View) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = AttractionListAdapter(context, data).apply {
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
        presenter?.getAttractions(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4))
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