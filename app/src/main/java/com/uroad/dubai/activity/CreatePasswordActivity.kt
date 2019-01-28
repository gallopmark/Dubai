package com.uroad.dubai.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.LoginPresenter
import com.uroad.dubai.api.view.LoginView
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.UserMDL
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_create_password.*

class CreatePasswordActivity : BaseActivity() ,LoginView{

    private var phone : String  = ""
    private var verificationcode : String = ""
    private var password : String = ""
    private var isInvisible : Boolean = false
    private var isForgotPassword : Boolean = false
    private lateinit var presenter : LoginPresenter

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_create_password,true)

        initView()
    }

    private fun initView() {
        intent.extras?.let {
            isForgotPassword = it.getBoolean("forgot", false)
            phone = it.getString("phone", "")
            verificationcode = it.getString("code", "")
        }

        presenter = LoginPresenter(this@CreatePasswordActivity)
        btnConfirm.setOnClickListener {
            password = edCreate.text.toString().trim()
            if (TextUtils.isEmpty(password)) {
                return@setOnClickListener
            }
            UserPreferenceHelper.saveUserPassword(this@CreatePasswordActivity, this.password)
            if (isForgotPassword){
                presenter.forgotPassword(WebApi.FORGOT_PASSWORD,WebApi.forgotPassword(phone,verificationcode,password))
            }
                finish()
        }

        ivInvisible.setOnClickListener {
            isInvisible = !isInvisible
            if (isInvisible) {
                edCreate.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(drawable(R.mipmap.icon_visual))
            } else {
                edCreate.transformationMethod = PasswordTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(drawable(R.mipmap.icon_invisible))
            }
            edCreate.setSelection(edCreate.text.length)
        }

        edCreate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val empty = edCreate.text.isNullOrEmpty()
                btnConfirm.isEnabled = !empty
                if (!empty)
                    btnConfirm.background = drawable(R.drawable.selector_verify_btn_bg)
                else
                    btnConfirm.background = drawable(R.drawable.shape_btn_bg_not_enabled)
            }

        })
        btnConfirm.isEnabled = !edCreate.text.isNullOrEmpty()
        btnConfirm.background = drawable(R.drawable.shape_btn_bg_not_enabled)
    }

    override fun loginSuccess(user: UserMDL?) { }
    override fun loginError(e: String) {
        showLongToast(e)
    }

    override fun getVerificationCode(verificationCode: String) {}
    override fun onShowLoading() {}
    override fun onHideLoading() {}
    override fun onShowError(msg: String?) {
        showLongToast(msg)
    }


}