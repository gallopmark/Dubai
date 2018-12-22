package com.uroad.dubai.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.library.utils.DisplayUtils

/**
 * @author MFB
 * @create 2018/12/17
 * @describe
 */
class NearMeTabAdapter(context: Context, mDatas: MutableList<String>)
    : BaseArrayRecyclerAdapter<String>(context, mDatas) {
    private var selected = 0
    private val ts16 = context.resources.getDimension(R.dimen.font_16)
    private val ts14 = context.resources.getDimension(R.dimen.font_14)
    private val margin = DisplayUtils.dip2px(context, 16f)

    override fun bindView(viewType: Int): Int = R.layout.item_nearmetab

    override fun onBindHoder(holder: RecyclerHolder, t: String, position: Int) {
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == itemCount - 1) {
            params.rightMargin = 0
        } else {
            params.rightMargin = margin
        }
        holder.itemView.layoutParams = params
        holder.setText(R.id.tvTab, t)
        if (position == selected) {
            holder.setTextSize(R.id.tvTab, TypedValue.COMPLEX_UNIT_PX, ts16)
            holder.setTypeface(R.id.tvTab, Typeface.defaultFromStyle(Typeface.BOLD))
        } else {
            holder.setTextSize(R.id.tvTab, TypedValue.COMPLEX_UNIT_PX, ts14)
            holder.setTypeface(R.id.tvTab, Typeface.defaultFromStyle(Typeface.NORMAL))
        }
    }

    fun setSelected(position: Int) {
        this.selected = position
        notifyDataSetChanged()
    }
}