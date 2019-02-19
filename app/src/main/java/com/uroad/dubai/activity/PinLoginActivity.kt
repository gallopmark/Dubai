package com.uroad.dubai.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.mapbox.core.utils.TextUtils
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

        initView()
    }

    private fun initView() {
        btnLogin.setOnClickListener {
            phone = edPhone.text.toString().trim()
            if (TextUtils.isEmpty(phone) || phone.length>11 || phone.length <6) {
                showLongToast("Phone number format is incorrect")
                return@setOnClickListener
            }

            openActivity(VerificationCodeActivity::class.java, Bundle().apply {
                putBoolean("isCreate", true)
                putString("phone", phone)
            })
        }

        tvCountry.setOnClickListener {
            val intent = Intent(this@PinLoginActivity, ChoiceCountryActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && data != null){
            val bundle = data.extras
            if (bundle != null){
                tvCountry.text = bundle.getString("name")
                tvAreText.text = "+${bundle.getString("phone")}"
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}