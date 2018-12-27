package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.ParkingMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe map parking detail dialog
 */
class ParkingDetailDialog : BaseMapPointDialog<ParkingMDL> {

    constructor(context: Context, mdl: ParkingMDL) : super(context, mdl)

    constructor(context: Context, data: MutableList<ParkingMDL>) : super(context, data)

    private var onNavigateListener: OnNavigateListener? = null

    fun setOnNavigateListener(onNavigateListener: OnNavigateListener?): ParkingDetailDialog {
        this.onNavigateListener = onNavigateListener
        return this
    }

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = ParkingAdapter(mContext, data)
    }

    private inner class ParkingAdapter(context: Context, data: MutableList<ParkingMDL>)
        : BaseArrayRecyclerAdapter<ParkingMDL>(context, data) {

        private val available = context.getString(R.string.available)
        private val total = context.getString(R.string.total)

        override fun bindView(viewType: Int): Int = R.layout.item_parkingdetail

        override fun onBindHoder(holder: RecyclerHolder, t: ParkingMDL, position: Int) {
            holder.setImageResource(R.id.ivIcon, t.getIcon())
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            var mAvailable = available
            t.available?.let { mAvailable += "\u3000$it" }
            holder.setText(R.id.tvAvailable, mAvailable)
            var mTotal = total
            t.total?.let { mTotal += "\u3000\u2000\u3000$it" }
            holder.setText(R.id.tvTotal, mTotal)
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
            holder.setOnClickListener(R.id.tvNavigation, View.OnClickListener { onNavigateListener?.onNavigate(t, this@ParkingDetailDialog) })
        }
    }

    interface OnNavigateListener {
        fun onNavigate(mdl: ParkingMDL, dialog: ParkingDetailDialog)
    }
}