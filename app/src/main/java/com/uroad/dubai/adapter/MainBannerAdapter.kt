package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter

/**
 * @author MFB
 * @create 2018/12/13
 * @describe
 */
class MainBannerAdapter(context: Context, pictures: MutableList<Int>) : BannerBaseArrayAdapter<Int>(context, pictures) {

    override fun bindView(viewType: Int): Int = R.layout.item_main_banner
    override fun bindHolder(holder: RecyclerHolder, t: Int, position: Int) {
        holder.setImageResource(R.id.ivPic, t)
    }

}