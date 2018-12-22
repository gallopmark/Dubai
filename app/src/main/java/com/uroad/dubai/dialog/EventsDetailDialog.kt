package com.uroad.dubai.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.EventsMDL

/**
 * @author MFB
 * @create 2018/12/19
 * @describe events detail dialog
 */
class EventsDetailDialog(private val context: Activity,
                         private val data: MutableList<EventsMDL>) : Dialog(context, R.style.TransparentDialog) {

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        window?.let { window ->
            val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_eventsdetail, LinearLayout(context), false)
            val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(recyclerView)
            recyclerView.adapter = EventDetailAdapter(context, data).apply {
                setOnItemChildClickListener(object : BaseRecyclerAdapter.OnItemChildClickListener {
                    override fun onItemChildClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                        if (view.id == R.id.ivClose) dismiss()
                    }
                })
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.OperateAnim)
            window.setGravity(Gravity.BOTTOM)
        }
    }

    private inner class EventDetailAdapter(context: Context, data: MutableList<EventsMDL>)
        : BaseArrayRecyclerAdapter<EventsMDL>(context, data) {
        override fun bindView(viewType: Int): Int = R.layout.item_eventsdetail

        override fun onBindHoder(holder: RecyclerHolder, t: EventsMDL, position: Int) {
            holder.setText(R.id.tvTitle, t.eventtypename)
            holder.setText(R.id.tvTime, t.updatetime)
            holder.setText(R.id.tvContent, t.roadtitle)
            holder.setText(R.id.tvStatus, t.statusname)
            holder.setText(R.id.tvDetail, t.reportout)
            holder.setText(R.id.tvStartTime, t.occtime)
            holder.setText(R.id.tvHandleTime, t.handletime)
            holder.setText(R.id.tvEndTime, t.realovertime)
            holder.bindChildClick(R.id.ivClose)
        }
    }
}