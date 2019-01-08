package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActicity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_verify,true)

        btnNext.setOnClickListener { openActivity(VerificationCodeActivity::class.java) }
    }

}