package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.model.RouteMDL
import com.uroad.library.utils.DisplayUtils

class FavoriteEventFmListAdapter(private val context: Context, data: MutableList<EventsMDL>)
      : BaseArrayRecyclerAdapter<EventsMDL>(context,data) {
    private val imageWith = DisplayUtils.getWindowWidth(context) / 3
    private val imageHeight = imageWith * 3 / 4
    private val params = RelativeLayout.LayoutParams(imageWith, imageHeight)
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    override fun bindView(viewType: Int): Int = R.layout.item_favorites_event

    override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
        //holder.setLayoutParams(R.id.ivPic, params)
        //holder.displayImage(R.id.ivPic, t.headimg, R.color.color_f7, RoundedCorners(dp4))
    }

}