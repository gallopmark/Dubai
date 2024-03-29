package com.uroad.dubai.adapter

import android.content.Context
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.ParkingMDL
import com.uroad.library.utils.DisplayUtils

class ParkingListAdapter(context: Context, data: MutableList<ParkingMDL>)
      : BaseArrayRecyclerAdapter<ParkingMDL>(context,data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) * 2/ 5
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val canLoading : Boolean = UserPreferenceHelper.canLoadingPhotos(context)
    override fun onBindHoder(holder: RecyclerHolder, t: ParkingMDL, position: Int) {
        holder.setLayoutParams(R.id.ivPic, params)
        if (canLoading){
            holder.displayImage(R.id.ivPic, t.imgPath, R.color.color_f7, RoundedCorners(dp4))
        }else{
            holder.displayImage(R.id.ivPic, "", R.color.color_f7, RoundedCorners(dp4))
        }
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvContext, t.content)
        holder.setText(R.id.tvDistance, t.distance)
        holder.setText(R.id.tvNum, t.num)
    }

    override fun bindView(viewType: Int): Int = R.layout.item_parking
}