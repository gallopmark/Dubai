package com.uroad.dubai.adapter

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.NewsMDL
import com.uroad.library.utils.DisplayUtils

/**
 * @author MFB
 * @create 2018/12/21
 * @describe new list adapter
 */
class HotelListCardAdapter(context: Context, data: MutableList<NewsMDL>)
    : BaseArrayRecyclerAdapter<NewsMDL>(context, data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 3
    private val imageHeight = imageWith * 3 / 4
    private val params = LinearLayout.LayoutParams(imageWith, imageHeight).apply { gravity = Gravity.CENTER }
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val canLoading : Boolean = UserPreferenceHelper.canLoadingPhotos(context)
    override fun bindView(viewType: Int): Int = R.layout.item_hotellist_card

    override fun onBindHoder(holder: RecyclerHolder, t: NewsMDL, position: Int) {
        holder.setLayoutParams(R.id.ivPic, params)
        if (canLoading){
            holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
        }else{
            holder.displayImage(R.id.ivPic, "", R.color.color_f7, RoundedCorners(dp4))
        }
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvTime, t.hours)
    }
}