package com.uroad.dubai.adapter

import android.content.Context
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.BusStopMDL
import com.uroad.dubai.model.PoliceMDL

class BusStopListAdapter(context: Context, data: MutableList<BusStopMDL>)
      : BaseArrayRecyclerAdapter<BusStopMDL>(context,data) {

    override fun onBindHoder(holder: RecyclerHolder, t: BusStopMDL, position: Int) {

    }

    override fun bindView(viewType: Int): Int = R.layout.item_busstop
}