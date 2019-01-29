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
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeAttractionsFragment : NearMeBaseFragment<AttractionPresenter>(), AttractionView {
    companion object {
        fun newInstance(longitude: Double, latitude: Double): NearMeAttractionsFragment {
            return NearMeAttractionsFragment().apply {
                arguments = Bundle().apply {
                    putDouble("longitude", longitude)
                    putDouble("latitude", latitude)
                }
            }
        }
    }

    private val data = ArrayList<ScenicMDL>()
    private lateinit var adapter: AttractionListCardAdapter

    override fun createPresenter(): AttractionPresenter = AttractionPresenter(this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initBundle()
        initRv()
    }

    private fun initBundle() {
        arguments?.let {
            longitude = it.getDouble("longitude", 0.0)
            latitude = it.getDouble("latitude", 0.0)
        }
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = AttractionListCardAdapter(context, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    openActivity(ScenicDetailActivity::class.java, Bundle().apply { putString("newsId", data[position].newsid) })
                }
            })
        }
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter.getAttractions(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4, longitude, latitude))
    }

    override fun onGetAttraction(attractions: MutableList<ScenicMDL>) {
        addDistance(attractions)
        this.data.clear()
        this.data.addAll(attractions)
        adapter.notifyDataSetChanged()
    }

    private fun addDistance(list: MutableList<ScenicMDL>) {
        val array = arrayOf("1.2km", "1.7km", "2.1km", "5.6km")
        for (i in 0 until list.size) {
            if (i < array.size) {
                list[i].distance = array[i]
            } else {
                val pos = i % array.size
                list[i].distance = array[pos]
            }
        }
    }

    override fun onShowError(msg: String?) {
        onRetry()
    }
}