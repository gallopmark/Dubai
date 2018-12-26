package com.uroad.dubai.dialog

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

abstract class BaseMapPointDialog<T> : Dialog {
    var mContext: Context
    var data: MutableList<T>

    constructor(context: Context, t: T) : super(context, R.style.TransparentDialog) {
        this.mContext = context
        data = ArrayList<T>().apply { add(t) }
    }

    constructor(context: Context, data: MutableList<T>) : super(context, R.style.TransparentDialog) {
        this.mContext = context
        this.data = data
    }

    override fun show() {
        super.show()
        initWindow()
    }

    private fun initWindow() {
        window?.let { window ->
            val contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_mappoint_detail, LinearLayout(mContext), false)
            val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(mContext).apply { orientation = LinearLayoutManager.HORIZONTAL }
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(recyclerView)
            onInitialization(recyclerView)
            window.setContentView(contentView)
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.OperateAnim)
            window.setGravity(Gravity.BOTTOM)
        }
    }

    abstract fun onInitialization(recyclerView: RecyclerView)

    interface OnViewClickListener {
        fun <T> onViewClick(view: View, viewType: Int, t: T, dialog: Dialog)
    }
}