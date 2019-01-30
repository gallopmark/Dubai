package com.uroad.dubai.adapter

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.model.NewsMDL

/**
 * @author MFB
 * @create 2018/12/21
 * @describe new list adapter
 */
class HotelListCardAdapter(context: Context, data: MutableList<NewsMDL>)
    : NearByBaseAdapter<NewsMDL>(context, data) {
    private val params = LinearLayout.LayoutParams(imageWidth, imageHeight).apply { gravity = Gravity.CENTER }
    override fun bindView(viewType: Int): Int = R.layout.item_hotellist_card

    override fun onBindHoder(holder: RecyclerHolder, t: NewsMDL, position: Int) {
        holder.setLayoutParams(R.id.mCoverIv, params)
        holder.displayImage(R.id.mCoverIv, t.headimg, R.color.color_f7, RoundedCorners(corners))
        holder.setText(R.id.mTitleTv, t.title)
        holder.setText(R.id.mAddressTv, t.address)
        holder.setText(R.id.mTimeTv, t.hours)
        holder.setText(R.id.mDistanceTv, t.getMDistance())
    }
}