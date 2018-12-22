package com.uroad.dubai.fragment

import android.os.Bundle
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseFragment
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 我的
 */
class MineFragment : BaseFragment() {
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mine)
        initTopLayout()
    }

    private fun initTopLayout() {
        val paddingTop = llUserInfo.paddingTop + DisplayUtils.getStatusHeight(context)
        llUserInfo.setPadding(llUserInfo.paddingStart, paddingTop, llUserInfo.paddingEnd, llUserInfo.paddingBottom)
    }
}