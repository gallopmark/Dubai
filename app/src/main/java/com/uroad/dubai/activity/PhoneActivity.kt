package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_phone.*

class PhoneActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_phone,true)

        btnNext.setOnClickListener { openActivity(VerifyActicity::class.java) }
    }

}