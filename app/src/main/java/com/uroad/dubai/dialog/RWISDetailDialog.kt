package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.RWISMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe RWIS detail dialog
 */
class RWISDetailDialog : BaseMapPointDialog<RWISMDL> {
    constructor(context: Context, mdl: RWISMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<RWISMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = RWISDetailAdapter(mContext, data)
    }

    private inner class RWISDetailAdapter(context: Context, data: MutableList<RWISMDL>)
        : BaseArrayRecyclerAdapter<RWISMDL>(context, data) {
        override fun bindView(viewType: Int): Int = R.layout.item_rwisdetail

        override fun onBindHoder(holder: RecyclerHolder, t: RWISMDL, position: Int) {
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            holder.setImageResource(R.id.ivPic, R.mipmap.ic_rwis_round)
            holder.setText(R.id.tvRt, t.reporttime)
            holder.setText(R.id.tvWs, t.windspeed)
            holder.setText(R.id.tvVis, t.visibility)
            holder.setText(R.id.tvSd, t.skyconditions)
            holder.setText(R.id.tvTem, t.temperature)
            holder.setText(R.id.tvDp, t.dewpoint)
            holder.setText(R.id.tvRh, t.relativehumidity)
        }
    }
}