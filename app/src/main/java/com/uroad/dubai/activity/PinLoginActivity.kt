package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_pin_login.*

class PinLoginActivity : BaseActivity(){

    var phone : String = ""

    override fun setUp(savedInstanceState: Bundle?) {
        baseToolbar.navigationIcon = drawable(R.mipmap.icon_finish)
        setBaseContentView(R.layout.activity_pin_login,true)
        withOption(getString(R.string.password_login), onClickListener = View.OnClickListener {
            finish()
        },color = Color.GRAY)

        btnLogin.setOnClickListener {
            phone = edPhone.text.toString().trim()
            if (phone.isEmpty()) phone = "123"//return@setOnClickListener

            openActivity(VerificationCodeActivity::class.java,Bundle().apply {
                putBoolean("isCreate",true)
                putString("phone",phone)
            })
        }
    }

}