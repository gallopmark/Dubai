package com.uroad.dubai.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.content.pm.PackageInfoCompat
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.dialog.WelcomeDialog
import com.uroad.dubai.fragment.MainFragment
import com.uroad.dubai.fragment.MineFragment
import com.uroad.dubai.fragment.TravelFragment
import com.uroad.dubai.local.AppSource

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        private const val TAB_HOME = "home"
        private const val TAB_TRAVEL = "travel"
        private const val TAB_MINE = "mine"
    }

    val arrayOf = arrayOf(Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR)

    override fun setUp(savedInstanceState: Bundle?) {
        requestWindowFullScreen()
        setBaseContentViewWithoutTitle(R.layout.activity_main, true)
        initTab()
        onWelcome()
    }

    private fun initTab() {
        requestPermissions()
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

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this@MainActivity,
                            arrayOf[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this@MainActivity,
                            arrayOf[1]) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(arrayOf, 2000)
            }
        }
    }
}
