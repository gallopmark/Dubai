package com.uroad.dubai.adapter

import android.content.Context
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.PoliceMDL
import android.annotation.SuppressLint


class PoliceListAdapter(private val context: Context, data: MutableList<PoliceMDL>)
      : BaseArrayRecyclerAdapter<PoliceMDL>(context,data) {

    @SuppressLint("MissingPermission")
    override fun onBindHoder(holder: RecyclerHolder, t: PoliceMDL, position: Int) {
        holder.bindChildClick(R.id.tvCallPol)
    }

    override fun bindView(viewType: Int): Int = R.layout.item_police
}