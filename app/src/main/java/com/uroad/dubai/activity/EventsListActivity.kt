package com.uroad.dubai.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.fragment.FavoriteEventFragment
import kotlinx.android.synthetic.main.activity_event.*

class EventsListActivity : BaseActivity() {

    private val mTabTitleList = ArrayList<String>()

    private val mFragmentList = ArrayList<Fragment>()

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_event)
        withTitle(getString(R.string.nearMe_events))
        initTab()
    }

    private fun initTab(){
        mFragmentList.add(FavoriteEventFragment())
        mFragmentList.add(FavoriteEventFragment())
        mFragmentList.add(FavoriteEventFragment())
        mViewPager.adapter = MyPagerAdapter(supportFragmentManager,mFragmentList)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(position: Int) {
                onPagerChange(position)
            }
        })

        tvAttrNearMe.setOnClickListener {
            val currentItem = mViewPager.currentItem
            if (currentItem != 0)
                mViewPager.currentItem = 0
        }

        tvAccidents.setOnClickListener {
            val currentItem = mViewPager.currentItem
            if (currentItem != 1)
                mViewPager.currentItem = 1
        }

        tvRoadworks.setOnClickListener {
            val currentItem = mViewPager.currentItem
            if (currentItem != 2)
                mViewPager.currentItem = 2
        }

        onPagerChange(0)
    }

    private fun onPagerChange(position : Int){
        if (position == 0){
            titleStatus(tvAttrNearMe,viewTvAttrNearMeInd,true)
            titleStatus(tvAccidents,viewTvAccidents,false)
            titleStatus(tvRoadworks,viewTvRoadworks,false)
            return
        }else if (position == 1){
            titleStatus(tvAttrNearMe,viewTvAttrNearMeInd,false)
            titleStatus(tvAccidents,viewTvAccidents,true)
            titleStatus(tvRoadworks,viewTvRoadworks,false)
            return
        }
        titleStatus(tvAttrNearMe,viewTvAttrNearMeInd,false)
        titleStatus(tvAccidents,viewTvAccidents,false)
        titleStatus(tvRoadworks,viewTvRoadworks,true)
    }

    private fun titleStatus(tv: TextView, view: View, isSelect: Boolean){
        val ts14 = resources.getDimension(R.dimen.font_14)
        val ts16 =resources.getDimension(R.dimen.font_16)
        if (isSelect){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts16)
            tv.isSelected = false
            view.visibility = View.VISIBLE
            return
        }
        tv.isSelected = false
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts14)
        //tv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        view.visibility = View.INVISIBLE
    }

    class MyPagerAdapter(fm : FragmentManager, private var fragmentList: List<Fragment>) : FragmentPagerAdapter(fm){
        override fun getItem(p0: Int): Fragment = fragmentList[p0]

        override fun getCount(): Int = fragmentList.size

    }

}