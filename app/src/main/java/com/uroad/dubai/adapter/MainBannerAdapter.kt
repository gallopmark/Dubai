package com.uroad.dubai.adapter

import android.content.Context
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.model.ScenicMDL
import com.uroad.glidev4.GlideV4
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter
import com.uroad.library.utils.DisplayUtils

/**
 * @author MFB
 * @create 2018/12/13
 * @describe
 */
class MainBannerAdapter(private val context: Context, data: MutableList<ScenicMDL>)
    : BannerBaseArrayAdapter<ScenicMDL>(context, data) {
    private val itemHeight = DisplayUtils.getWindowHeight(context) * 3 / 10
    override fun bindView(viewType: Int): Int = R.layout.item_main_banner
    override fun bindHolder(holder: RecyclerHolder, t: ScenicMDL, position: Int) {
        holder.itemView.layoutParams = holder.itemView.layoutParams.apply { height = itemHeight }
        val ivPic = holder.obtainView<ImageView>(R.id.ivPic)
        GlideV4.getInstance().displayImage(context, t.headimg, ivPic)
    }
}