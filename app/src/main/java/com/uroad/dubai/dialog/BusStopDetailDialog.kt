package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.BusStopMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe map bus stop detail dialog
 */
class BusStopDetailDialog : BaseMapPointDialog<BusStopMDL> {
    constructor(context: Context, mdl: BusStopMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<BusStopMDL>) : super(context, data)

    private var onNavigateListener: OnNavigateListener? = null

    fun setOnNavigateListener(onNavigateListener: OnNavigateListener?): BusStopDetailDialog {
        this.onNavigateListener = onNavigateListener
        return this
    }

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = BusStopDetailAdapter(mContext, data)
    }

    private inner class BusStopDetailAdapter(context: Context, data: MutableList<BusStopMDL>)
        : BaseArrayRecyclerAdapter<BusStopMDL>(context, data) {
        override fun bindView(viewType: Int): Int = R.layout.item_busstopdetail

        override fun onBindHoder(holder: RecyclerHolder, t: BusStopMDL, position: Int) {
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
            holder.setImageResource(R.id.ivIcon, R.mipmap.ic_busstop_round)
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            holder.setOnClickListener(R.id.tvNavigation, View.OnClickListener { onNavigateListener?.onNavigate(t, this@BusStopDetailDialog) })
        }
    }

    interface OnNavigateListener {
        fun onNavigate(mdl: BusStopMDL, dialog: BusStopDetailDialog)
    }
}