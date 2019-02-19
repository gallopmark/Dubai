package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseMapBoxLocationActivity
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.fragment.AttractionNearFragment
import kotlinx.android.synthetic.main.activity_attraction.*
import java.lang.Exception

class AttractionsListActivity : BaseMapBoxLocationActivity() {

    private var mTitle: String? = null
    private var type :String? = null

    /**
     * 存放 tab 标题
     */
    private val mTabTitleList = ArrayList<String>()

    private val mFragmentList = ArrayList<Fragment>()
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_attraction)
        val bundle = intent.extras
        openLocation()
        bundle?.let {
            type = it.getString("userstatus")
        }
        when (type) {
            NewsType.HOTEL.code -> withTitle(getString(R.string.travel_menu_hotels))      //酒店
            NewsType.RESTAURANT.code -> withTitle(getString(R.string.travel_menu_restaurants)) //餐厅
            else -> withTitle(getString(R.string.travel_menu_attractions))      //1001004 景点
        }
    }

    override fun afterLocation(location: Location) {
        longitude = location.longitude
        latitude = location.latitude
        initTab()
    }

    override fun onLocationFailure(exception: Exception) {
        initTab()
    }

    private fun initTab(){
        mTabTitleList.add("Near me")
        mTabTitleList.add("All")
        mFragmentList.add(AttractionNearFragment.newInstance(longitude,latitude,"$type"))
        mFragmentList.add(AttractionNearFragment.newInstance(0.0,0.0,"$type"))
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

        tvAttrAll.setOnClickListener {
            val currentItem = mViewPager.currentItem
            if (currentItem != 1)
                mViewPager.currentItem = 1
        }

        ivSearchAttr.setOnClickListener {
            showTipsDialog(getString(R.string.developing))
        }

        onPagerChange(0)
    }

   /* private fun getFragment(code : Int) : AttractionNearFragment{
        val nearFragment = AttractionNearFragment()
        val bundle = Bundle()
        bundle.putString("userstatus",type)
        //nearFragment.arguments?.putBundle("bundle",bundle)
        nearFragment.arguments = bundle
        return nearFragment
    }*/

    private fun onPagerChange(position : Int){
        if (position == 0){
            titleStatus(tvAttrNearMe,viewTvAttrNearMeInd,true)
            titleStatus(tvAttrAll,viewTvAttrAllInd,false)
            return
        }
        titleStatus(tvAttrNearMe,viewTvAttrNearMeInd,false)
        titleStatus(tvAttrAll,viewTvAttrAllInd,true)
    }

    private fun titleStatus(tv:TextView, view: View, isSelect: Boolean){
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