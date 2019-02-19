package com.uroad.dubai.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.View
import com.google.android.gms.common.*
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.AppVersionPresenter
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.dialog.WelcomeDialog
import com.uroad.dubai.enumeration.MessageType
import com.uroad.dubai.fragment.MainFragment
import com.uroad.dubai.fragment.MineFragment
import com.uroad.dubai.fragment.TravelFragment
import com.uroad.dubai.local.AppSource
import com.uroad.dubai.push.Constants
import com.uroad.dubai.utils.DubaiUtils
import com.uroad.library.compat.AppDialog

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasePresenterActivity<AppVersionPresenter<MainActivity>>() {

    override fun createPresenter(): AppVersionPresenter<MainActivity> = AppVersionPresenter(this, this)

    companion object {
        private const val TAB_HOME = "home"
        private const val TAB_TRAVEL = "travel"
        private const val TAB_MINE = "mine"
    }

    override fun setUp(savedInstanceState: Bundle?) {
        requestWindowFullScreen()
        setBaseContentViewWithoutTitle(R.layout.activity_main, true)
        initTab()
        initBundle()
        onWelcome()
        checkGoogleService()
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

    private fun initBundle() {
        val bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE)
        bundle?.let {
            var detailIntent: Intent? = null
            val messageId = it.getString(Constants.PUSH_MESSAGE_ID)
            val messageType = it.getString(Constants.PUSH_MESSAGE_TYPE)
            when (messageType) {
                MessageType.EVENT.CODE -> {
                    detailIntent = Intent(this, EventsDetailActivity::class.java).apply { putExtras(Bundle().apply { putString("eventId", messageId) }) }
                }
                MessageType.NOTICE.CODE -> {
                    detailIntent = Intent(this, NoticeListActivity::class.java)
                }
                MessageType.NEWS.CODE -> {
                    detailIntent = Intent(this, NewsDetailsActivity::class.java).apply { putExtras(Bundle().apply { putString("newsId", messageId) }) }
                }
                MessageType.SYSTEM.CODE -> {
                    detailIntent = Intent(this, MessagesListActivity::class.java)
                }
            }
            detailIntent?.let { openActivity(it) }
        }
    }

    private fun onWelcome() {
        if (!AppSource.isWelcome(this)) {
            WelcomeDialog(this).show()
            AppSource.saveWelcome(this)
        }
    }

    private fun checkGoogleService() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            showDialog(R.string.without_google_services, R.string.install, object : SimpleDialogInterface() {
                override fun onConfirm(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                    DubaiUtils.openGoogleServices(this@MainActivity)
                }
            })
        }
    }

    override fun initData() {
        presenter.checkVersion()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog(R.string.quit_app_tips, object : SimpleDialogInterface() {
                override fun onConfirm(v: View, dialog: AppDialog) {
                    dialog.dismiss()
                    finish()
                }
            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
