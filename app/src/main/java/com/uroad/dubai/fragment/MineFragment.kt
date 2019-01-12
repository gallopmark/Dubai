package com.uroad.dubai.fragment

import android.os.Bundle
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.common.BaseFragment
import com.uroad.library.utils.DisplayUtils
import com.uroad.dubai.local.UserPreferenceHelper
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 我的
 */
class MineFragment : BaseFragment() {

    private var isLogin : Boolean = false

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mine)
        initTopLayout()

        isLogin = UserPreferenceHelper.isLogin(context)
        tvMessage.setOnClickListener{
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        tvFavorites.setOnClickListener{
            if (check()) return@setOnClickListener
            openActivity(FavoriteListActivity::class.java)
        }
        tvCalendar.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(CalendarListActivity::class.java)
        }
        tvSetting.setOnClickListener { openActivity(SettingActivity::class.java) }
    }

    private fun initTopLayout() {
        val paddingTop = llUserInfo.paddingTop + DisplayUtils.getStatusHeight(context)
        llUserInfo.setPadding(llUserInfo.paddingStart, paddingTop, llUserInfo.paddingEnd, llUserInfo.paddingBottom)
    }

    private fun check() : Boolean{
        isLogin = UserPreferenceHelper.isLogin(context)
        if (!isLogin){
            openActivity(LoginActivity::class.java)
            return true
        }
        return false
    }
}