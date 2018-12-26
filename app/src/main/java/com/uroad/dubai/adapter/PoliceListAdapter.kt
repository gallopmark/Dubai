package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.PoliceMDL

class PoliceListAdapter(context: Context, data: MutableList<PoliceMDL>)
      : BaseArrayRecyclerAdapter<PoliceMDL>(context,data) {

    override fun onBindHoder(holder: RecyclerHolder, t: PoliceMDL, position: Int) {

    }

    override fun bindView(viewType: Int): Int = R.layout.item_police
}