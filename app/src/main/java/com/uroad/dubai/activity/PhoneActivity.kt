package com.uroad.dubai.activity

import android.content.Intent
import android.os.Bundle
import com.mapbox.core.utils.TextUtils
import com.uroad.dubai.R
import com.uroad.library.common.BaseActivity
import kotlinx.android.synthetic.main.activity_phone.*

class PhoneActivity : BaseActivity() {

    var phone : String = ""
    var isCreate : Boolean = false
    var forgot : Boolean = false

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_phone,true)

        initView()
    }

    private fun initView() {
        intent.extras?.let { it ->
            isCreate = it.getBoolean("isCreate", false)
            forgot = it.getBoolean("forgot", false)
        }
        btnNext.setOnClickListener {
            phone = edPhone.text.toString().trim()
            if (TextUtils.isEmpty(phone) || phone.length>11 || phone.length <6) {
                showLongToast("Phone number format is incorrect")
                return@setOnClickListener
            }

            if (isCreate || forgot)
                openActivity(VerificationCodeActivity::class.java, Bundle().apply {
                    putBoolean("isCreate", isCreate)
                    putBoolean("forgot", forgot)
                    putString("phone", phone)
                })
            else
                openActivity(VerifyActivity::class.java, Bundle().apply {
                    putString("phone", phone)
                    putBoolean("forgot",forgot)
                })
            finish()
        }

        tvCountry.setOnClickListener {
            val intent = Intent(this@PhoneActivity, ChoiceCountryActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && data != null){
            val bundle = data.extras
            if (bundle != null){
                tvCountry.text = bundle.getString("name")
                tvCountryCode.text = "+${bundle.getString("phone")}"
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}