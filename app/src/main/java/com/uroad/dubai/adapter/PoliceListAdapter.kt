package com.uroad.dubai.adapter

import android.content.Context
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.PoliceMDL
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri


class PoliceListAdapter(context: Context, data: MutableList<PoliceMDL>)
      : BaseArrayRecyclerAdapter<PoliceMDL>(context,data) {

    @SuppressLint("MissingPermission")
    override fun onBindHoder(holder: RecyclerHolder, t: PoliceMDL, position: Int) {
        val text = holder.obtainView<TextView>(R.id.tvPoliceTel).text
        holder.obtainView<TextView>(R.id.tvCallPol).setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:12345678901"))//直接拨打电话
            context.startActivity(dialIntent)
        }

    }

    override fun bindView(viewType: Int): Int = R.layout.item_police
}