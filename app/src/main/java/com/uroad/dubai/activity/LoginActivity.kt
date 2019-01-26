package com.uroad.dubai.activity

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.LoginPresenter
import com.uroad.dubai.api.view.LoginView
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.UserMDL
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.library.utils.DeviceUtils
import com.uroad.library.utils.MD5Utils
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() , LoginView {

    private var localAccount  : String? = ""
    private var localPassword : String? = ""
    private var account : String = ""
    private var password : String = ""
    private var isInvisible : Boolean = false
    private lateinit var presenter : LoginPresenter

    override fun setUp(savedInstanceState: Bundle?) {
        baseToolbar.navigationIcon = drawable(R.mipmap.icon_finish)
        setBaseContentView(R.layout.activity_login,true)
        withOption(getString(R.string.pin_login), onClickListener = View.OnClickListener {
            openActivity(PinLoginActivity::class.java)
        },color = Color.GRAY)

        initView()
        login()
    }

    private fun initView() {
        presenter = LoginPresenter(this@LoginActivity)
        ivInvisible.setOnClickListener {
            isInvisible = !isInvisible
            if (isInvisible) {
                edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(drawable(R.mipmap.icon_visual))
            } else {
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(drawable(R.mipmap.icon_invisible))
            }
            edPassword.setSelection(edPassword.text.length)
        }

        tvForgot.setOnClickListener {
            openActivity(PhoneActivity::class.java, Bundle().apply {
                putBoolean("forgot", true)
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
                //account = "123"
                return@setOnClickListener
            }

            /*if (!TextUtils.equals(account, localAccount) || !TextUtils.equals(password, localPassword)) {
                //password = "123"
                return@setOnClickListener
            }*/

            presenter.login(WebApi.USER_LOGIN,
                    WebApi.login("2", this.account,
                            MD5Utils.encoderByMd5(password),
                            PackageInfoUtils.getVersionName(this@LoginActivity),
                            DeviceUtils.getAndroidID(this@LoginActivity),
                            DeviceUtils.getManufacturer(),
                            "Android",
                            "${DeviceUtils.getSDKVersion()}"))

        }
    }

    override fun loginSuccess(user: UserMDL?) {
        user?.let {

        }
        UserPreferenceHelper.saveAccount(this@LoginActivity, account)
        UserPreferenceHelper.saveUserPassword(this@LoginActivity, password)
        UserPreferenceHelper.login(this@LoginActivity)

        openActivity(MainActivity::class.java)
    }

    override fun loginError(e: String) {
        showLongToast(e)
    }

    override fun getVerificationCode(verificationCode: String) {

    }

    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {

    }

}
