package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.DMSysMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe DMS detail dialog
 */
class DMSDetailDialog : BaseMapPointDialog<DMSysMDL> {
    constructor(context: Context, mdl: DMSysMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<DMSysMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = DMSysAdapter(mContext, data).apply { }
    }

    private inner class DMSysAdapter(context: Context, data: MutableList<DMSysMDL>)
        : BaseArrayRecyclerAdapter<DMSysMDL>(context, data) {
        override fun bindView(viewType: Int): Int = R.layout.item_dmsysdetail

        override fun onBindHoder(holder: RecyclerHolder, t: DMSysMDL, position: Int) {
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
            holder.setImageResource(R.id.ivIcon, R.mipmap.ic_dms_round)
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            holder.setImageResource(R.id.ivPic, R.mipmap.ic_dmsyspic)
        }
    }
}