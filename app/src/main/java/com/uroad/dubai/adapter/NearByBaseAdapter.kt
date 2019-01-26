package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.library.utils.DisplayUtils

abstract class NearByBaseAdapter<T>(context: Context, data: MutableList<T>)
    : BaseArrayRecyclerAdapter<T>(context, data) {
    protected val imageWidth = DisplayUtils.getWindowWidth(context) / 5 * 2
    protected val imageHeight = imageWidth * 3 / 4
    protected val corners = DisplayUtils.dip2px(context,4f)
}