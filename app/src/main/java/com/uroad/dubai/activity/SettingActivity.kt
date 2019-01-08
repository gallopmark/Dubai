package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.settings))
        setBaseContentView(R.layout.activity_setting)

        tvPassword.setOnClickListener { openActivity(PhoneActivity::class.java) }
    }

}