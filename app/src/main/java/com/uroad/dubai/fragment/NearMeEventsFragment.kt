package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.EventsDetailActivity
import com.uroad.dubai.adapter.EventsListCardAdapter
import com.uroad.dubai.api.presenter.EventsPresenter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mainmearme.*

class NearMeEventsFragment : NearMeBaseFragment<EventsPresenter>(), EventsPresenter.EventsView {
    companion object {
        fun newInstance(longitude: Double, latitude: Double): NearMeEventsFragment {
            return NearMeEventsFragment().apply {
                arguments = Bundle().apply {
                    putDouble("longitude", longitude)
                    putDouble("latitude", latitude)
                }
            }
        }
    }

    private val data = ArrayList<EventsMDL>()
    private lateinit var adapter: EventsListCardAdapter
    override fun createPresenter(): EventsPresenter = EventsPresenter(this)
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainmearme)
        initRv()
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapter = EventsListCardAdapter(context, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until data.size) {
                        openActivity(EventsDetailActivity::class.java, Bundle().apply { putString("eventMDL", GsonUtils.fromObjectToJson(data[position])) })
                    }
                }
            })
        }
        recyclerView.adapter = adapter
    }

    override fun initData() {
        presenter.getEventList(1, 4, "", longitude, latitude)
    }

    override fun onGetEventList(data: MutableList<EventsMDL>) {
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onShowError(msg: String?) {
        onRetry()
    }
}