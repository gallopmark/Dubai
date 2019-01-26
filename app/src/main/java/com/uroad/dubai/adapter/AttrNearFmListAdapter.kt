package com.uroad.dubai.adapter

import android.content.Context
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.AttractionNearFMMDL
import com.uroad.library.utils.DisplayUtils

class AttrNearFmListAdapter(private val context: Context,type : String?, data: MutableList<AttractionNearFMMDL>)
      : BaseArrayRecyclerAdapter<AttractionNearFMMDL>(context,data) {
    private val typePage = type
    private val imageWith = DisplayUtils.getWindowWidth(context) * 2/ 5
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private val canLoading : Boolean = UserPreferenceHelper.canLoadingPhotos(context)
    override fun onBindHoder(holder: RecyclerHolder, t: AttractionNearFMMDL, position: Int) {
        holder.setLayoutParams(R.id.ivPic, params)
        if (canLoading){
            holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
        }else{
            holder.displayImage(R.id.ivPic, "", R.color.color_f7, RoundedCorners(dp4))
        }
        setText(holder,t,position)
    }

    override fun bindView(viewType: Int): Int = R.layout.item_attraction

    private fun setText(holder: RecyclerHolder,t: AttractionNearFMMDL,position: Int){
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvContext, "Location  ${t.address}")
        holder.setText(R.id.tvDistance, t.hours)
        holder.setText(R.id.tvNum, "Distance  ${t.commentstar?:"0"}km")
        /*when(typePage){
            "1001002" -> //酒店
            {
                holder.setText(R.id.tvNum, t.hours)
            }
            "1001003" ->//餐厅
            {
                holder.setText(R.id.tvDistance, t.hours)
            }
            else ->{//1001004 景点
                holder.setText(R.id.tvContext, "Location  ${t.address}")
                holder.setText(R.id.tvNum, "Distance  ${t.commentstar?:"0"}km")
            }
        }*/
    }

}