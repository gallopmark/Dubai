package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.LoginView
import com.uroad.dubai.model.UserMDL
import org.json.JSONObject

class LoginPresenter(val loginView: LoginView) : BasePresenter<LoginView>(loginView) {
    fun login(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(loginView) {
            override fun onHttpResultOk(data: String?) {
                loginView.loginSuccess(GsonUtils.fromDataBean(data, UserMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                loginView.loginError(errorMsg.toString())
            }
        })
    }

    fun sendVerificationCode(method: String?, params: HashMap<String, String?>){
        request(method, params, object : StringObserver(loginView) {
            override fun onHttpResultOk(data: String?) {
                val verificationcode = JSONObject(GsonUtils.getData(data)).getString("verificationcode")
                loginView.getVerificationCode(verificationcode?:"")
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                loginView.loginError(errorMsg.toString())
            }
        })
    }

    fun forgotPassword(method: String?, params: HashMap<String, String?>){
        request(method, params, object : StringObserver(loginView) {
            override fun onHttpResultOk(data: String?) {
                val verificationcode = JSONObject(data).getString("data")
                loginView.loginError(verificationcode)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                loginView.loginError(errorMsg.toString())
            }
        })
    }
}