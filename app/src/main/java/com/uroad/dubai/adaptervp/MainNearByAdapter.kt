package com.uroad.dubai.adaptervp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.uroad.dubai.fragment.*

class MainNearByAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val roadsFragment = NearMeRoadsFragment()
    private val eventsFragment = NearMeEventsFragment()
    private val newsFragment = NearMeNewsFragment()
    private val hotelFragment = NearMeHotelFragment()
    private val restaurantsFragment = NearMeRestaurantsFragment()
    private val attractionsFragment = NearMeAttractionsFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> roadsFragment
            1 -> eventsFragment
            2 -> newsFragment
            3 -> hotelFragment
            4 -> restaurantsFragment
            else -> attractionsFragment
        }
    }

    override fun getCount(): Int = 6
}
