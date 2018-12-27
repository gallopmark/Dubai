package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.PoliceMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe police detail dialog
 */
class PoliceDetailDialog : BaseMapPointDialog<PoliceMDL> {
    constructor(context: Context, mdl: PoliceMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<PoliceMDL>) : super(context, data)

    private var onNavigateListener: OnNavigateListener? = null
    fun setOnNavigateListener(onNavigateListener: OnNavigateListener?): PoliceDetailDialog {
        this.onNavigateListener = onNavigateListener
        return this
    }

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = PoliceDetailAdapter(context, data)
    }

    private inner class PoliceDetailAdapter(context: Context, data: MutableList<PoliceMDL>)
        : BaseArrayRecyclerAdapter<PoliceMDL>(context, data) {
        private val telephone = context.getString(R.string.telephone)
        override fun bindView(viewType: Int): Int = R.layout.item_policedetail

        override fun onBindHoder(holder: RecyclerHolder, t: PoliceMDL, position: Int) {
            holder.setOnClickListener(R.id.ivClose, View.OnClickListener { dismiss() })
            holder.setImageResource(R.id.ivIcon, R.mipmap.ic_police_round)
            holder.setText(R.id.tvTitle, t.title)
            holder.setText(R.id.tvContent, t.content)
            var mTelephone = telephone
            t.telphone?.let { mTelephone += "\u3000$it" }
            holder.setText(R.id.tvTelPhone, mTelephone)
            holder.setOnClickListener(R.id.tvNavigation, View.OnClickListener { onNavigateListener?.onNavigate(t, this@PoliceDetailDialog) })
        }
    }

    interface OnNavigateListener {
        fun onNavigate(mdl: PoliceMDL, dialog: PoliceDetailDialog)
    }
}