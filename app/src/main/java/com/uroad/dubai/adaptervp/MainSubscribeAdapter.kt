package com.uroad.dubai.adaptervp

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.library.widget.banner.BannerBaseAdapter

class MainSubscribeAdapter(private val context: Context,
                           private val data: MutableList<SubscribeMDL>) : PagerAdapter() {
    private var mDownTime: Long = 0
    private var mListener: OnPageTouchListener? = null
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int {
        return if (data.size == 0) 0 else Integer.MAX_VALUE
    }

    private fun getItem(position: Int): SubscribeMDL {
        return if (position >= data.size) data[0] else data[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var pos = 0
        if (data.size != 0) {
            pos = position % data.size
        }
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_subscribe_route, container, false)
        // 处理视图和数据
        convert(view, getItem(pos), pos)
        view.isClickable = true
        // 处理条目的触摸事件
        view.setOnTouchListener(View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    mDownTime = System.currentTimeMillis()
                    mListener?.onPageDown()
                }
                MotionEvent.ACTION_UP -> {
                    val upTime = System.currentTimeMillis()
                    mListener?.onPageUp()
                    if (upTime - mDownTime < 500) {
                        // 500毫秒以内就算单击
                        mListener?.onPageClick(pos, getItem(pos))
                    }
                }
            }
            return@OnTouchListener false
        })
        container.addView(view)
        return view
    }

    private fun convert(convertView: View, data: SubscribeMDL, position: Int) {
        val tvSubscribeName = convertView.findViewById<TextView>(R.id.tvSubscribeName)
        val tvRouteArrow = convertView.findViewById<TextView>(R.id.tvRouteArrow)
        val tvDistance = convertView.findViewById<TextView>(R.id.tvDistance)
        val tvTravelTime = convertView.findViewById<TextView>(R.id.tvTravelTime)
        tvSubscribeName.text = "Route"
        tvRouteArrow.text = data.getStartEndPoint()
        tvDistance.text = data.distance
        tvTravelTime.text = data.travelTime
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    /**
     * 条目页面的触摸事件
     */
    interface OnPageTouchListener {
        fun onPageClick(position: Int, mdl: SubscribeMDL)

        fun onPageDown()

        fun onPageUp()
    }

    fun setOnPageTouchListener(listener: OnPageTouchListener) {
        this.mListener = listener
    }
}