package com.uroad.dubai.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.activity.DetailsActivity
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.AttractionNearFMMDL
import com.uroad.dubai.model.ParkingMDL
import com.uroad.library.utils.DisplayUtils

class AttrNearFmListAdapter(context: Context, data: MutableList<AttractionNearFMMDL>)
      : BaseArrayRecyclerAdapter<AttractionNearFMMDL>(context,data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 3
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    override fun onBindHoder(holder: RecyclerHolder, t: AttractionNearFMMDL, position: Int) {
        holder.setLayoutParams(R.id.ivPic, params)
        holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
        holder.setText(R.id.tvTitle, t.title)
        holder.setText(R.id.tvContext, t.content)
        holder.setText(R.id.tvDistance, t.distance)
        //holder.setText(R.id.tvNum, t.num)
        holder.setVisibility(R.id.tvNum,false)

        holder.itemView.setOnClickListener {
            var intent = Intent(context, DetailsActivity::class.java)
            var bundle = Bundle()
            bundle.putString("title",t.title)
            bundle.putString("time",t.publishtime)
            bundle.putString("imgUrl",t.headimg)
            bundle.putString("content",t.content)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun bindView(viewType: Int): Int = R.layout.item_parking
}