package com.uroad.dubai.adapter

import android.content.Context
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.PoiSearchPoiMDL
import com.uroad.dubai.model.PoiSearchTextMDL

class PoiSearchHistoryAdapter(content: Context, private val data: MutableList<MultiItem>)
    : BaseArrayRecyclerAdapter<MultiItem>(content, data) {
    override fun getItemViewType(position: Int): Int = data[position].getItemType()
    override fun bindView(viewType: Int): Int =
            if (viewType == 1) R.layout.item_poisearch_text
            else R.layout.item_poisearch_poi

    override fun onBindHoder(holder: RecyclerHolder, t: MultiItem, position: Int) {
        val itemType = holder.itemViewType
        if (itemType == 1) {
            val mdl = t as PoiSearchTextMDL
            holder.setText(R.id.tvText, mdl.content)
        } else {
            val mdl = t as PoiSearchPoiMDL
            holder.setText(R.id.tvText, mdl.carmenFeature?.placeName())
        }
        if (position == itemCount - 1) {
            holder.setVisibility(R.id.vDivider, View.GONE)
        } else {
            holder.setVisibility(R.id.vDivider, View.VISIBLE)
        }
    }
}