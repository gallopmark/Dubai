package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.model.FavoritesMDL
import com.uroad.library.banner.adapter.BannerBaseArrayAdapter

class FavoritesAdapter(context: Context, data: MutableList<FavoritesMDL>)
    : BannerBaseArrayAdapter<FavoritesMDL>(context, data) {

    override fun bindView(viewType: Int): Int = R.layout.item_favorites_banner

    override fun bindHolder(holder: RecyclerHolder, t: FavoritesMDL, position: Int) {

    }
}