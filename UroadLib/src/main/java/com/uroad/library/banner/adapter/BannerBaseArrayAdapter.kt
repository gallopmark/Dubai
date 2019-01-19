package com.uroad.library.banner.adapter

import android.content.Context

abstract class BannerBaseArrayAdapter<T>(context: Context, var data: List<T>) : BannerBaseAdapter(context) {

    override fun onBindViewHolder(holder: BannerBaseAdapter.RecyclerHolder, position: Int) {
        val realPosition = position % data.size
        bindHolder(holder, data[realPosition], realPosition)
    }

    abstract fun bindHolder(holder: BannerBaseAdapter.RecyclerHolder, t: T, position: Int)

    override fun getItemCount(): Int {
        return if (data.isNullOrEmpty()) 0 else Integer.MAX_VALUE
    }
}
