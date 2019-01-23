package com.uroad.dubai.adaptervp

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.model.NewsMDL
import com.uroad.glidev4.GlideV4
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.widget.banner.BaseBannerAdapter

class MainBannerAdapter(private val context: Context, data: MutableList<NewsMDL>)
    : BaseBannerAdapter<NewsMDL>(context, data) {

    private val itemWidth = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 10f) * 2
    private val itemHeight = itemWidth / 2

    override fun bindView(viewType: Int): Int = R.layout.item_main_banner

    override fun convert(mConvertView: View, item: NewsMDL, realPosition: Int) {
        val ivPic = obtainView<ImageView>(R.id.ivPic)
        ivPic.layoutParams = ivPic.layoutParams.apply {
            width = itemWidth
            height = itemHeight
        }
        GlideV4.getInstance().displayImage(context, item.headimg, ivPic)
    }
}