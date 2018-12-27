package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.CCTVSnapMDL
import com.uroad.library.utils.DisplayUtils

/**
 * @author MFB
 * @create 2018/12/26
 * @describe cctv detail dialog
 */
class CCTVSnapDetailDialog : BaseMapPointDialog<CCTVSnapMDL> {
    constructor(context: Context, mdl: CCTVSnapMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<CCTVSnapMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = CCTVDetailAdapter(mContext, data)
    }

    private inner class CCTVDetailAdapter(private val context: Context, data: MutableList<CCTVSnapMDL>)
        : BaseArrayRecyclerAdapter<CCTVSnapMDL>(context, data) {

        override fun bindView(viewType: Int): Int = R.layout.item_cctvsnapdetail

        override fun onBindHoder(holder: RecyclerHolder, t: CCTVSnapMDL, position: Int) {
            holder.setImageResource(R.id.ivIcon, R.mipmap.ic_cctv_round)
            holder.setText(R.id.tvTitle, t.shortname)
            holder.setText(R.id.tvContent, t.resname)
            val recyclerView = holder.obtainView<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            recyclerView.adapter = CCTVPicAdapter(context, ArrayList<Int>().apply {
                add(R.mipmap.ic_cctvpic1)
                add(R.mipmap.ic_cctvpic2)
            })
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
        }
    }

    private inner class CCTVPicAdapter(context: Context, data: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, data) {
        private val needWidth = if (data.size > 1) {
            (DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 10f) * 3) / 2
        } else {
            DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 10f) * 2
        }

        private val needHeight = needWidth * 2 / 3
        private val dp10 = DisplayUtils.dip2px(context, 10f)

        override fun bindView(viewType: Int): Int = R.layout.item_cctvpic

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.setImageResource(R.id.ivPic, t)
            val params = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply {
                width = needWidth
                height = needHeight
            }
            if (position in 0..(itemCount - 1)) {
                params.rightMargin = dp10
            } else {
                params.rightMargin = 0
            }
            holder.itemView.layoutParams = params
        }
    }

}