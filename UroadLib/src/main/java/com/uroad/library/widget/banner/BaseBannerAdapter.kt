package com.uroad.library.widget.banner

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * @author MFB
 * @create 2019/1/23
 * @describe  ViewPager基类适配器，需要传入Item视图和数据
 */
abstract class BaseBannerAdapter<T>(private val context: Context, var data: MutableList<T>) : PagerAdapter() {
    private lateinit var convertView: View
    private var mDownTime: Long = 0
    private var onItemClickListener: OnItemClickListener<T>? = null

    override fun getCount(): Int = if (data.isNotEmpty()) Integer.MAX_VALUE else data.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var realPosition = position
        if (data.isNotEmpty()) {
            realPosition %= data.size
        }
        val itemType = getItemViewType(realPosition)
        this.convertView = LayoutInflater.from(context).inflate(bindView(itemType), container, false)
        convertView.isClickable = true
        // 处理视图和数据
        convert(convertView, getItem(realPosition), realPosition)
        // 处理条目的触摸事件
        convertView.setOnClickListener { onItemClickListener?.onItemClick(getItem(realPosition), position) }
        container.addView(convertView)
        return convertView
    }

    abstract fun convert(mConvertView: View, item: T, realPosition: Int)

    open fun getItemViewType(position: Int): Int {
        return 0
    }

    abstract fun bindView(viewType: Int): Int

    fun <T : View> obtainView(id: Int): T {
        return convertView.findViewById(id)
    }

    fun setText(id: Int, text: CharSequence?) {
        obtainView<TextView>(id).text = text
    }

    fun setImageResource(id: Int, resId: Int) {
        obtainView<ImageView>(id).setImageResource(resId)
    }

    fun setVisibility(id: Int, visibility: Int) {
        obtainView<View>(id).visibility = visibility
    }

    fun setBackgroundColor(id: Int, color: Int) {
        obtainView<View>(id).setBackgroundColor(color)
    }

    fun setBackgroundColorRes(id: Int, color: Int) {
        obtainView<View>(id).setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun setBackgroundResource(id: Int, resId: Int) {
        obtainView<View>(id).setBackgroundResource(resId)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    open fun getItem(position: Int): T {
        return if (position >= data.size) data[0] else data[position]
    }

    interface OnItemClickListener<T> {
        fun onItemClick(t: T, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }
}