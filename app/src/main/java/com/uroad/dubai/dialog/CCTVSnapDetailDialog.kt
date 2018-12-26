package com.uroad.dubai.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.uroad.dubai.model.CCTVSnapMDL

/**
 * @author MFB
 * @create 2018/12/26
 * @describe cctv detail dialog
 */
class CCTVSnapDetailDialog : BaseMapPointDialog<CCTVSnapMDL> {
    constructor(context: Context, mdl: CCTVSnapMDL) : super(context, mdl)
    constructor(context: Context, data: MutableList<CCTVSnapMDL>) : super(context, data)

    override fun onInitialization(recyclerView: RecyclerView) {

    }

}