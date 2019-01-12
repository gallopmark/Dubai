package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.ScenicMDL

class AttractionCardAdapter(context: Context, data: MutableList<ScenicMDL>)
    : BaseArrayRecyclerAdapter<ScenicMDL>(context, data) {
    override fun bindView(viewType: Int): Int = R.layout.item_attraction_card

    override fun onBindHoder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7)
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvContent, t.content)
        holder.setText(R.id.tvAddress, t.address)
        holder.setText(R.id.tvDistance, t.distance)
    }
}