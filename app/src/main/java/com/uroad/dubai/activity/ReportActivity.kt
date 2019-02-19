package com.uroad.dubai.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.fragment.ReportFCFragment
import com.uroad.dubai.fragment.ReportQRSFragment
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : BaseActivity() {
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_report)
        withTitle(getString(R.string.report))
        initFragments()
        initView()
        setCurrentTab(0, true)
    }

    private fun initFragments() {
        val fragments = ArrayList<Fragment>().apply {
            add(ReportQRSFragment())
            add(ReportFCFragment())
        }
        viewPager.adapter = MPageAdapter(supportFragmentManager, fragments)
    }

    private fun initView() {
        mRlTab1.setOnClickListener { setCurrentTab(0, true) }
        mRlTab2.setOnClickListener { setCurrentTab(1, true) }
        mReportIv.setOnClickListener { openActivity(ReportSubmitActivity::class.java) }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                setCurrentTab(position, false)
            }

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }
        })
    }

    private fun setCurrentTab(tab: Int, scrollPage: Boolean) {
        setTab(tab, scrollPage)
        if (tab == 0) {
            resetStatus(1)
        } else {
            resetStatus(0)
        }
    }

    private fun setTab(tab: Int, scrollPage: Boolean) {
        val sp14 = resources.getDimension(R.dimen.space_14)
        val color33 = ContextCompat.getColor(this, R.color.appTextColor)
        if (tab == 0) {
            vTab1.visibility = View.VISIBLE
            mTab1.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp14)
            mTab1.setTextColor(color33)
        } else {
            vTab2.visibility = View.VISIBLE
            mTab2.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp14)
            mTab2.setTextColor(color33)
        }
        if (scrollPage) viewPager.currentItem = tab
    }

    private fun resetStatus(tab: Int) {
        val sp12 = resources.getDimension(R.dimen.space_12)
        val colorGary = ContextCompat.getColor(this, R.color.gary)
        if (tab == 0) {
            vTab1.visibility = View.INVISIBLE
            mTab1.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp12)
            mTab1.setTextColor(colorGary)
        } else {
            vTab2.visibility = View.INVISIBLE
            mTab2.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp12)
            mTab2.setTextColor(colorGary)
        }
    }

    private class MPageAdapter(fm: FragmentManager, private val fragments: MutableList<Fragment>)
        : FragmentPagerAdapter(fm) {

        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int): Fragment = fragments[position]
    }
}