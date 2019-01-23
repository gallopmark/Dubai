package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.NoticeMDL

class NoticeListAdapter(context: Context, data: MutableList<NoticeMDL>)
    : BaseArrayRecyclerAdapter<NoticeMDL>(context, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_notice

    override fun onBindHoder(holder: RecyclerHolder, t: NoticeMDL, position: Int) {
        holder.setImageResource(R.id.ivIcon, R.mipmap.ic_notice_drawable)
        holder.setText(R.id.mTitleTextView, t.content)
        holder.setText(R.id.mTimeTextView, t.publishtime)
    }
}