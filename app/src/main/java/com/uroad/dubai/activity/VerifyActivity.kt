package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : BaseActivity() {

    var phone : String? = null

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_verify,true)

        intent.extras?.let {
            phone = it.getString("phone")
            tvSelected.text = "+971 $phone"
        }
        btnNext.setOnClickListener {
            openActivity(VerificationCodeActivity::class.java,Bundle().apply {
                putString("phone",phone)
            })
            finish()
        }

        tvReplace.setOnClickListener {
            openActivity(PhoneActivity::class.java)
            finish()
        }
    }

}