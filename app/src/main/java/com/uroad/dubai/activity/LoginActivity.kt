package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.zhgs.helper.UserPreferenceHelper
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var localAccount  : String? = ""
    private var localPassword : String? = ""
    private var account : String? = ""
    private var password : String? = ""
    private var isInvisible : Boolean = false

    @SuppressLint("NewApi")
    override fun setUp(savedInstanceState: Bundle?) {
        baseToolbar.navigationIcon = getDrawable(R.mipmap.icon_finish)
        setBaseContentView(R.layout.activity_login,true)
        withOption(getString(R.string.pin_login), onClickListener = View.OnClickListener {
            openActivity(PinLoginActivity::class.java)
        },color = Color.GRAY)

        initView()
        login()
    }

    @SuppressLint("NewApi")
    private fun initView() {
        ivInvisible.setOnClickListener {
            isInvisible = !isInvisible
            if (isInvisible) {
                edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(getDrawable(R.mipmap.icon_visual))
            } else {
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(getDrawable(R.mipmap.icon_invisible))
            }
            edPassword.setSelection(edPassword.text.length)
        }

        tvForgot.setOnClickListener {
            openActivity(PhoneActivity::class.java, Bundle().apply {
                putBoolean("isCreate", true)
            })
        }

        tvCreate.setOnClickListener {
            openActivity(PhoneActivity::class.java, Bundle().apply {
                putBoolean("isCreate", true)
            })
        }
    }

    private fun login() {
        localAccount = UserPreferenceHelper.getAccount(this@LoginActivity)
        localPassword = UserPreferenceHelper.getUserPassword(this@LoginActivity)

        btnLogin.setOnClickListener {
            account = edAccount.text.toString().trim()
            password = edPassword.text.toString().trim()

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                return@setOnClickListener
            }

            if (!TextUtils.equals(account, localAccount) || !TextUtils.equals(password, localPassword)) {
                return@setOnClickListener
            }

            UserPreferenceHelper.saveAccount(this@LoginActivity, account!!)
            UserPreferenceHelper.saveUserPassword(this@LoginActivity, password!!)
            UserPreferenceHelper.login(this@LoginActivity)

            openActivity(MainActivity::class.java)
        }
    }

}
