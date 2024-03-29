package com.uroad.dubai.adaptervp

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.utils.TimeUtils
import com.uroad.dubai.utils.DubaiUtils
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.widget.banner.BaseBannerAdapter

class MainSubscribeAdapter(private val context: Context,
                           private val data: MutableList<SubscribeMDL>)
    : BaseBannerAdapter<SubscribeMDL>(context, data) {

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return if (data.size > 1) Integer.MAX_VALUE else data.size
    }

    override fun bindView(viewType: Int): Int = R.layout.item_subscribe_route

    override fun convert(mConvertView: View, item: SubscribeMDL, realPosition: Int) {
        val tvSubscribeName = mConvertView.findViewById<TextView>(R.id.tvSubscribeName)
        val tvRouteArrow = mConvertView.findViewById<TextView>(R.id.tvRouteArrow)
        val tvDistance = mConvertView.findViewById<TextView>(R.id.tvDistance)
        val tvTravelTime = mConvertView.findViewById<TextView>(R.id.tvTravelTime)
        val recyclerView = mConvertView.findViewById<RecyclerView>(R.id.recyclerView)
        tvSubscribeName.text = "Route"
        tvRouteArrow.text = item.getStartEndPoint()
        var distance = ""
        item.distance?.let { distance = DubaiUtils.convertDistance(it.toInt()) }
        tvDistance.text = distance
        var travelTime = ""
        item.travelTime?.let { travelTime = TimeUtils.convertSecond2Min(it.toInt()) }
        tvTravelTime.text = travelTime
        recyclerView.layoutManager = getLayoutManager()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = RouteColorAdapter(context, item.getRouteColors(context))
        if (item.getRouteColors(context).size > 0) {
            recyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.GONE
        }
    }

    private fun getLayoutManager(): LinearLayoutManager {
        val layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        return layoutManager
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private class RouteColorAdapter(context: Context, data: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, data) {
        private val width = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 16f) * 2
        private val routeWidth = Math.ceil(width.toDouble() / data.size).toInt()
        override fun bindView(viewType: Int): Int = R.layout.item_routeline

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.setBackgroundColor(R.id.vRouteLine, t)
            holder.itemView.layoutParams = holder.itemView.layoutParams.apply { width = routeWidth }
        }
    }
}