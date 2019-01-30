package com.uroad.dubai.adapter

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.model.ScenicMDL


class AttractionListCardAdapter(context: Context, data: MutableList<ScenicMDL>)
    : NearByBaseAdapter<ScenicMDL>(context, data) {
    private var params = LinearLayout.LayoutParams(imageWidth, imageHeight).apply { gravity = Gravity.CENTER }

    override fun bindView(viewType: Int): Int = R.layout.item_attractionlist_card

    override fun onBindHoder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        holder.setLayoutParams(R.id.mCoverIv, params)
        holder.displayImage(R.id.mCoverIv, t.headimg, R.color.color_f7, RoundedCorners(corners))
        holder.setText(R.id.mTitleTv, t.title)
        holder.setText(R.id.mAddressTv, t.address)
        holder.setText(R.id.mTimeTv, t.hours)
        holder.setText(R.id.mDistanceTv, t.getMDistance())
    }
}