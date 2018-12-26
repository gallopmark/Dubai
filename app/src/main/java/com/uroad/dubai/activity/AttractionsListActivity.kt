package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.uroad.dubai.R
import com.uroad.dubai.adapter.BaseFragmentAdapter
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.fragment.RankFragment
import com.uroad.dubai.utils.TabUtils
import kotlinx.android.synthetic.main.activity_attraction.*

class AttractionsListActivity : BaseActivity() {

    private var mTitle: String? = null

    /**
     * 存放 tab 标题
     */
    private val mTabTitleList = ArrayList<String>()

    private val mFragmentList = ArrayList<Fragment>()

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.travel_menu_attractions))
        setBaseContentView(R.layout.activity_attraction)
        initTab()
    }

    private fun initTab(){
        mTabTitleList.add("Near me")
        mTabTitleList.add("All")
        mFragmentList.add(RankFragment())
        mFragmentList.add(RankFragment())
        mViewPager.adapter = BaseFragmentAdapter(supportFragmentManager,mFragmentList,mTabTitleList)

    }

}