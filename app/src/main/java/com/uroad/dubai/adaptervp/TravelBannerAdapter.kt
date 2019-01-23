package com.uroad.dubai.adaptervp

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.model.NewsMDL
import com.uroad.glidev4.GlideV4
import com.uroad.glidev4.listener.ImageSize
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.widget.banner.BaseBannerAdapter

class TravelBannerAdapter(private val context: Context, data: MutableList<NewsMDL>)
    : BaseBannerAdapter<NewsMDL>(context, data) {
    private val width = DisplayUtils.getWindowWidth(context)
    private val height = (width * 0.67).toInt()
    override fun bindView(viewType: Int): Int = R.layout.item_travel_banner

    override fun convert(mConvertView: View, item: NewsMDL, realPosition: Int) {
        val ivPic = obtainView<ImageView>(R.id.ivPic)
        GlideV4.getInstance().displayImage(context, item.headimg, ivPic, ImageSize(width, height))
    }
}