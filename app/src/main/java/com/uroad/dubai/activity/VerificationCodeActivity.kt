package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_verify_code.*

class VerificationCodeActivity : BaseActivity() {

    var code : String? = ""
    var hasContent : Boolean = false

    @SuppressLint("LogNotTimber")
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_verify_code,true)

        btnVerify.setOnClickListener {
            edVerify.verifyCodeText?.let { it ->
                hasContent = it.length == 6
                code = it
            }
        }
    }
}