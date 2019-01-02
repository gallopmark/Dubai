package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity

/**
 * @author MFB
 * @create 2018/12/29
 * @describe more function activity
 */
class MoreActivity : BaseActivity() {
    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.home_menu_more))
        setBaseContentView(R.layout.activity_more)
    }
}