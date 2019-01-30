package com.uroad.dubai.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.AppVersionPresenter
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.dialog.WelcomeDialog
import com.uroad.dubai.fragment.MainFragment
import com.uroad.dubai.fragment.MineFragment
import com.uroad.dubai.fragment.TravelFragment
import com.uroad.dubai.local.AppSource
import com.uroad.dubai.model.VersionMDL
import com.uroad.dubai.service.VersionUpdateService
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.library.compat.AppDialog
import com.uroad.library.utils.VersionUtils

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasePresenterActivity<AppVersionPresenter>(), AppVersionPresenter.AppVersionView {

    override fun createPresenter(): AppVersionPresenter = AppVersionPresenter(this)

    companion object {
        private const val TAB_HOME = "home"
        private const val TAB_TRAVEL = "travel"
        private const val TAB_MINE = "mine"
    }

    private val handler = Handler()

    override fun setUp(savedInstanceState: Bundle?) {
        requestWindowFullScreen()
        setBaseContentViewWithoutTitle(R.layout.activity_main, true)
        initTab()
        onWelcome()
    }

    private fun initTab() {
        radioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                R.id.rbHome -> setCurrentTab(1)
                R.id.rbTravel -> setCurrentTab(2)
                R.id.rbMine -> setCurrentTab(3)
            }
        }
        setCurrentTab(1)
    }

    private fun setCurrentTab(tab: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (tab) {
            1 -> {
                val mainFragment = supportFragmentManager.findFragmentByTag(TAB_HOME)
                if (mainFragment == null) {
                    transaction.add(R.id.container, MainFragment(), TAB_HOME)
                } else {
                    transaction.show(mainFragment)
                }
            }
            2 -> {
                val travelFragment = supportFragmentManager.findFragmentByTag(TAB_TRAVEL)
                if (travelFragment == null) {
                    transaction.add(R.id.container, TravelFragment(), TAB_TRAVEL)
                } else {
                    transaction.show(travelFragment)
                }
            }
            3 -> {
                val mineFragment = supportFragmentManager.findFragmentByTag(TAB_MINE)
                if (mineFragment == null) {
                    transaction.add(R.id.container, MineFragment(), TAB_MINE)
                } else {
                    transaction.show(mineFragment)
                }
            }
        }
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        for (fragment in supportFragmentManager.fragments) transaction.hide(fragment)
    }

    private fun onWelcome() {
        if (!AppSource.isWelcome(this)) {
            WelcomeDialog(this).show()
            AppSource.saveWelcome(this)
        }
    }

    override fun initData() {
        presenter.checkVersion()
    }

    override fun getVersion(mdl: VersionMDL?) {
        mdl?.let { checkOrUpdate(it) }
    }

    private fun checkOrUpdate(mdl: VersionMDL) {
        var version = ""
        mdl.version?.let { version = it }
        if (VersionUtils.isNeedUpdate(version, PackageInfoUtils.getVersionName(this))) {
            showDialog(getString(R.string.dialog_default_title), mdl.versioncontent,
                    getString(R.string.version_update_cancel),
                    getString(R.string.version_update_confirm),
                    object : DialogViewClickListener {
                        override fun onConfirm(v: View, dialog: AppDialog) {
                            dialog.dismiss()
                            startUpdate(mdl.url)
                        }

                        override fun onCancel(v: View, dialog: AppDialog) {
                            dialog.dismiss()
                        }
                    })
        }
    }

    private fun startUpdate(url: String?) {
        if (TextUtils.isEmpty(url)) showShortToast(getString(R.string.version_update_error_url))
        else {
            startService(Intent(this@MainActivity, VersionUpdateService::class.java).apply { putExtra("downloadUrl", url) })
        }
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog(getString(R.string.dialog_default_title), getString(R.string.quit_app_tips)
                    , getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), object : DialogViewClickListener {
                override fun onConfirm(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                    finish()
                }

                override fun onCancel(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                }
            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
