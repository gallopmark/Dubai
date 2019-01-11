package com.uroad.dubai.activity

import android.os.Bundle
import com.mapbox.core.utils.TextUtils
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_phone.*

class PhoneActivity : BaseActivity() {

    var phone : String = ""
    var isCreate : Boolean = false

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_phone,true)

        intent.extras?.let {it->
            isCreate = it.getBoolean("isCreate", false)
        }
        btnNext.setOnClickListener {
            phone = edPhone.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                return@setOnClickListener
            }

            if (isCreate)
                openActivity(VerificationCodeActivity::class.java,Bundle().apply {
                    putBoolean("isCreate",isCreate)
                    putString("phone",phone)
                })
            else
                openActivity(VerifyActicity::class.java,Bundle().apply {
                    putString("phone",phone)
                })
            finish()
        }
    }

}