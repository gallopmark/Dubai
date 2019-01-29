package com.uroad.dubai.adapter

import android.app.Activity
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.widget.LinearLayout
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.utils.TimeUtils
import com.uroad.dubai.utils.DubaiUtils
import com.uroad.library.utils.DisplayUtils

class DirectionsRouteAdapter(context: Activity, routes: MutableList<DirectionsRoute>)
    : BaseArrayRecyclerAdapter<DirectionsRoute>(context, routes) {
    private val params = if (routes.size > 3) {
        LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
    } else {
        LinearLayout.LayoutParams(DisplayUtils.getWindowWidth(context) / routes.size, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
    private var selected: Int = 0
    private val sp14 = context.resources.getDimensionPixelOffset(R.dimen.font_14)
    private var onItemSelectedListener: OnItemSelectedListener? = null

    override fun bindView(viewType: Int): Int = R.layout.item_directionsroute

    override fun onBindHoder(holder: RecyclerHolder, t: DirectionsRoute, position: Int) {
        val duration = t.duration()
        if (duration != null) {
            val source = TimeUtils.convertSecond2Min(duration.toInt())
            val ss = SpannableString(source).apply {
                val index = if (source.contains("s")) source.indexOf("s") else source.indexOf("m")
                setSpan(AbsoluteSizeSpan(sp14, false), index, source.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            holder.setText(R.id.tvDuration, ss)
        } else {
            holder.setText(R.id.tvDuration, "")
        }
        val distance = t.distance()
        if (distance != null) {
            holder.setText(R.id.tvDistance, DubaiUtils.convertDistance(distance.toInt()))
        } else {
            holder.setText(R.id.tvDistance, "")
        }
        holder.itemView.layoutParams = params
        holder.itemView.setOnClickListener {
            setSelected(position)
            onItemSelectedListener?.onItemSelected(t, position)
        }
        holder.itemView.isSelected = selected == position
    }

    fun setSelected(position: Int) {
        this.selected = position
        notifyDataSetChanged()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(t: DirectionsRoute, position: Int)
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener
    }
}