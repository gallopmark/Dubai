package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.MoreActivity
import com.uroad.dubai.activity.NewsListActivity
import com.uroad.dubai.activity.RoadNavigationActivity
import com.uroad.dubai.activity.RoadsListActivity
import com.uroad.dubai.adapter.FavoritesAdapter
import com.uroad.dubai.adapter.NearMeTabAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.model.FavoritesMDL
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.content_mainnearby.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.home_content_scroll.*
import kotlinx.android.synthetic.main.home_top_collapse.*
import kotlinx.android.synthetic.main.home_top_expand.*
import kotlinx.android.synthetic.main.home_top_flexhead.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 首页
 */
class MainFragment : BaseFragment() {

    private var statusHeight = 0

    companion object {
        private const val TAG_ROADS = "roads"
        private const val TAG_EVENTS = "events"
        private const val TAG_NEWS = "news"
        private const val TAG_HOTEL = "hotel"
        private const val TAG_RESTAURANTS = "restaurants"
        private const val TAG_ATTRACTIONS = "attractions"
    }

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_main)
        statusHeight = DisplayUtils.getStatusHeight(context)
        initLayout()
        initMenu()
        initBanner()
        initNotice()
        initFavorites()
        initNearBy()
    }

    private fun initLayout() {
        ablBar.setPadding(0, statusHeight, 0, 0)
//        tlCollapse.layoutParams = (tlCollapse.layoutParams as Toolbar.LayoutParams).apply { topMargin = statusHeight }
        ablBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            if (offset <= total / 2) {
                tlExpand.visibility = View.VISIBLE
                tlCollapse.visibility = View.GONE
            } else {
                tlExpand.visibility = View.GONE
                tlCollapse.visibility = View.VISIBLE
            }
        })
    }

    private fun initMenu() {
        ivMessage.setOnClickListener { }
        ivMessageColl.setOnClickListener { }
        ivSearch.setOnClickListener { }
        ivSearchColl.setOnClickListener { }
        tvNavigation.setOnClickListener { openActivity(RoadNavigationActivity::class.java) }
        ivNavigation.setOnClickListener { openActivity(RoadNavigationActivity::class.java) }
        tvHighWay.setOnClickListener { openActivity(RoadsListActivity::class.java) }
        ivHighWay.setOnClickListener { openActivity(RoadsListActivity::class.java) }
        tvNews.setOnClickListener { openActivity(NewsListActivity::class.java) }
        ivNews.setOnClickListener { openActivity(NewsListActivity::class.java) }
        tvMore.setOnClickListener { openActivity(MoreActivity::class.java) }
        ivMore.setOnClickListener { openActivity(MoreActivity::class.java) }
    }

    private fun initBanner() {
        childFragmentManager.beginTransaction().replace(R.id.flBanner, MainBannerFragment()).commitAllowingStateLoss()
    }

    private fun initNotice() {
        tvNotice.text = "Extended floating bridge hours and free toll period AI Maktoum Bridge"
    }

    private fun initFavorites() {
        bannerFavorites.isNestedScrollingEnabled = false
        val favorites = ArrayList<FavoritesMDL>().apply {
            add(FavoritesMDL())
            add(FavoritesMDL())
            add(FavoritesMDL())
            add(FavoritesMDL())
        }
        bannerFavorites.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        bannerFavorites.adapter = FavoritesAdapter(context, favorites)
        bannerFavorites.initFlingSpeed(9000).initPageParams(-10, 35)
                .initPosition(0)
                .setAnimFactor(0.1f)
                .autoPlay(false)
    }

    private fun initNearBy() {
        rvNearByTab.isNestedScrollingEnabled = false
        rvNearByTab.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        val tabs = ArrayList<String>().apply {
            add(context.getString(R.string.nearMe_roads))
            add(context.getString(R.string.nearMe_events))
            add(context.getString(R.string.nearMe_news))
            add(context.getString(R.string.nearMe_hotel))
            add(context.getString(R.string.nearMe_restaurants))
            add(context.getString(R.string.nearMe_attractions))
        }
        rvNearByTab.adapter = NearMeTabAdapter(context, tabs).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until tabs.size) {
                        setSelected(position)
                        rvNearByTab.smoothScrollToPosition(position)
                        setCurrentTab(position)
                    }
                }
            })
        }
        initFragments()
    }

    private fun initFragments() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flNearMeRoads, NearMeRoadsFragment(), TAG_ROADS)
        transaction.replace(R.id.flNearMeEvents, NearMeEventsFragment(), TAG_EVENTS)
        transaction.replace(R.id.flNearMeNews, NearMeNewsFragment(), TAG_NEWS)
        transaction.replace(R.id.flNearMeHotel, NearMeHotelFragment(), TAG_HOTEL)
        transaction.replace(R.id.flNearMeRestaurants, NearMeRestaurantsFragment(), TAG_RESTAURANTS)
        transaction.replace(R.id.flNearMeAttractions, NearMeAttractionsFragment(), TAG_ATTRACTIONS)
        transaction.commitAllowingStateLoss()
        setCurrentTab(0)
    }

    private fun setCurrentTab(tab: Int) {
        hideFragments()
        when(tab){
            0 ->  flNearMeRoads.visibility = View.VISIBLE
            1 ->  flNearMeEvents.visibility = View.VISIBLE
            2 ->  flNearMeNews.visibility = View.VISIBLE
            3 ->  flNearMeHotel.visibility = View.VISIBLE
            4 ->  flNearMeRestaurants.visibility = View.VISIBLE
            5 ->  flNearMeAttractions.visibility = View.VISIBLE
        }
//        val transaction = childFragmentManager.beginTransaction()
//        hideFragments(transaction)
//        when (tab) {
//            0 -> childFragmentManager.findFragmentByTag(TAG_ROADS)?.let { transaction.show(it) }
//            1 -> childFragmentManager.findFragmentByTag(TAG_EVENTS)?.let { transaction.show(it) }
//            2 -> childFragmentManager.findFragmentByTag(TAG_NEWS)?.let { transaction.show(it) }
//            3 -> childFragmentManager.findFragmentByTag(TAG_HOTEL)?.let { transaction.show(it) }
//            4 -> childFragmentManager.findFragmentByTag(TAG_RESTAURANTS)?.let { transaction.show(it) }
//            5 -> childFragmentManager.findFragmentByTag(TAG_ATTRACTIONS)?.let { transaction.show(it) }
//        }
//        transaction.commit()
    }

    private fun hideFragments() {
        flNearMeRoads.visibility = View.GONE
        flNearMeEvents.visibility = View.GONE
        flNearMeNews.visibility = View.GONE
        flNearMeHotel.visibility = View.GONE
        flNearMeRestaurants.visibility = View.GONE
        flNearMeAttractions.visibility = View.GONE
    }
}