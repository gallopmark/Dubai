package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.NewsMDL

class NewsDetailDialog : BaseMapPointDialog<NewsMDL> {

    private var onDialogViewClickListener: OnDialogViewClickListener? = null

    constructor(context: Context, mdl: NewsMDL) : super(context, mdl)

    constructor(context: Context, data: MutableList<NewsMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {
        recyclerView.adapter = NewsDetailAdapter(mContext, data)
    }

    private inner class NewsDetailAdapter(context: Context, data: MutableList<NewsMDL>)
        : BaseArrayRecyclerAdapter<NewsMDL>(context, data) {

        override fun bindView(viewType: Int): Int = R.layout.item_newsdetail

        override fun onBindHoder(holder: RecyclerHolder, t: NewsMDL, position: Int) {
            holder.setImageResource(R.id.mIconImageView, t.getIcon())
            holder.setText(R.id.mTitleTextView, t.title)
            holder.setText(R.id.mAddressTv, t.address)
            holder.setText(R.id.mTimeTextView, t.hours)
            holder.setText(R.id.mPhoneTextView, t.phone)
            holder.setOnClickListener(R.id.mCloseImageView, View.OnClickListener { dismiss() })
            holder.setOnClickListener(R.id.mDetailTextView, View.OnClickListener { onDialogViewClickListener?.onDetailClick(this@NewsDetailDialog) })
            holder.setOnClickListener(R.id.mNavigationTextView, View.OnClickListener { onDialogViewClickListener?.onNavigation(t, this@NewsDetailDialog) })
        }
    }

    interface OnDialogViewClickListener {
        fun onDetailClick(dialog: NewsDetailDialog)
        fun onNavigation(mdl: NewsMDL, dialog: NewsDetailDialog)
    }

    fun setOnDialogViewClickListener(onDialogViewClickListener: OnDialogViewClickListener?): NewsDetailDialog {
        this.onDialogViewClickListener = onDialogViewClickListener
        return this
    }
}