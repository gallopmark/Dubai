package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.local.AppSource
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.library.compat.AppDialog
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : BaseActivity() {

    private var isLogin: Boolean = false
    private var phone: String = ""

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.settings))
        setBaseContentView(R.layout.activity_setting)
        isLogin = UserPreferenceHelper.isLogin(this@SettingActivity)
        phone = UserPreferenceHelper.getAccount(this@SettingActivity)
        initView()
    }

    private fun initView() {
        tvLanguage.setOnClickListener {
            // 杀掉进程
            /*android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)*/
            showTipsDialog(getString(R.string.developing))
        }

        tvPassword.setOnClickListener {
            if (!isLogin) {
                openActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            openActivity(VerifyActivity::class.java, Bundle().apply {
                putString("phone", phone)
            })
        }
        btnLogOut.setOnClickListener {
            showDialog(getString(R.string.tips), getString(R.string.logout_tips), object : DialogViewClickListener {
                override fun onCancel(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                }

                override fun onConfirm(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                    UserPreferenceHelper.logOut(this@SettingActivity)
                    //UserPreferenceHelper.clear(this@SettingActivity)
                    openActivity(LoginActivity::class.java)
                    finish()
                }

            })
        }

        tvAppTips.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvAbout.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvTerm.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvOffline.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvNavigationAddress.setOnClickListener {
            if (isLogin) openActivity(NavigationAddressActivity::class.java)
            else openActivity(LoginActivity::class.java)
        }

        swSearchHistory.setOnCheckedChangeListener { _, isChecked -> AppSource.setShowSearchHistory(this@SettingActivity, isChecked) }
        swLoadingPhoto.setOnCheckedChangeListener { _, isChecked -> AppSource.setCanLoadPhoto(this@SettingActivity, isChecked) }
        if (!isLogin) btnLogOut.visibility = View.GONE
        swSearchHistory.isChecked = AppSource.isShowSearchHistory(this)
        swLoadingPhoto.isChecked = AppSource.isCanLoadPhoto(this)
    }

    @SuppressLint("NewApi")
    private fun switchLanguage(language: String) {
        val resources = resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        when (language) {
            "zh" -> {
                config.locale = Locale.CHINESE
                resources.updateConfiguration(config, dm)
            }
            "en" -> {
                config.locale = Locale.ENGLISH
                resources.updateConfiguration(config, dm)
            }
            "ar" -> {
                config.locale = Locale.forLanguageTag("ar")
                resources.updateConfiguration(config, dm)
            }
            else -> {
                //config.locale = Locale.US
                config.setLocale(Locale.US)
                resources.updateConfiguration(config, dm)

            }
        }
    }

}