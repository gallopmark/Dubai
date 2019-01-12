package com.uroad.dubai.adaptervp

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.uroad.dubai.R

class GuideAdapter(private val context: Context,
                   private val pictures: MutableList<Int>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = pictures.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_splash_guide, container, false)
        val ivPic = view.findViewById<ImageView>(R.id.ivPic)
        ivPic.setImageResource(pictures[position])
        container.addView(ivPic)
        return ivPic
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}