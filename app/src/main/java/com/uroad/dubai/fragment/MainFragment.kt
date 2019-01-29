package com.uroad.dubai.fragment

import android.animation.ObjectAnimator
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.view.View
import com.uroad.dubai.activity.*
import com.uroad.dubai.adapter.NearMeTabAdapter
import com.uroad.dubai.api.presenter.MessagesPresenter
import com.uroad.dubai.api.view.MessagesView
import com.uroad.dubai.common.BaseMapBoxLocationFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.utils.AnimUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.content_mainnearby.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.home_content_scroll.*
import kotlinx.android.synthetic.main.home_top_collapse.*
import kotlinx.android.synthetic.main.home_top_expand.*
import kotlinx.android.synthetic.main.home_top_flexhead.*
import java.lang.Exception


/**
 * @author MFB
 * @create 2018/12/12
 * @describe 首页
 */
class MainFragment : BaseMapBoxLocationFragment(), MessagesView {

    private var myLocation: Location? = null
    private var currentTab = 0
    private var isFirstCommit = true
    private var updateCount = 0
    private lateinit var presenter: MessagesPresenter
    private lateinit var handler: Handler
    private var animator: ObjectAnimator? = null

    companion object {
        private const val TAG_BANNER = "banner"
        private const val TAG_NOTICE = "notice"
        private const val TAG_FAVORITES = "favorites"
        private const val TAG_ROADS = "roads"
        private const val TAG_EVENTS = "events"
        //        private const val TAG_NEWS = "news"
        private const val TAG_HOTEL = "hotel"
        private const val TAG_RESTAURANTS = "restaurants"
        private const val TAG_ATTRACTIONS = "attractions"
    }

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(com.uroad.dubai.R.layout.fragment_main)
        initLayout()
        initMenu()
        initBanner()
        initNotice()
        initFavorites()
        initNearBy()
        presenter = MessagesPresenter(this)
        handler = Handler()
    }

    private fun initLayout() {
        ablBar.setPadding(0, DisplayUtils.getStatusHeight(context), 0, 0)
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
        refreshLayout.setOnRefreshListener { onRefresh() }
    }

    private fun initMenu() {
        ivMessage.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        ivMessageColl.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        ivSearch.setOnClickListener { showTipsDialog(getString(com.uroad.dubai.R.string.developing)) }
        ivSearchColl.setOnClickListener { showTipsDialog(getString(com.uroad.dubai.R.string.developing)) }
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
        childFragmentManager.beginTransaction().replace(com.uroad.dubai.R.id.flBanner, MainBannerFragment().apply {
            setRequestCallback(object : FragmentRequestCallback {
                override fun onRequestFinish() {
                    onFinishRefresh()
                }
            })
        }, TAG_BANNER).commitAllowingStateLoss()
    }

    private fun initNotice() {
        childFragmentManager.beginTransaction().replace(com.uroad.dubai.R.id.flNotice, MainNoticeFragment().apply {
            setOnRequestCallback(object : MainNoticeFragment.OnRequestCallback {
                override fun callback(isEmpty: Boolean) {
                    if (isEmpty) this@MainFragment.flNotice.visibility = View.GONE
                    else this@MainFragment.flNotice.visibility = View.VISIBLE
                    onFinishRefresh()
                }

                override fun onFailure() {
                    onFinishRefresh()
                }
            })
        }, TAG_NOTICE).commitAllowingStateLoss()
    }

    private fun initFavorites() {
        childFragmentManager.beginTransaction().replace(com.uroad.dubai.R.id.flFavorites, MainFavoritesFragment().apply {
            setOnRequestCallback(object : MainFavoritesFragment.OnRequestCallback {
                override fun callback(isEmpty: Boolean) {
                    if (isEmpty) this@MainFragment.flFavorites.visibility = View.GONE
                    else this@MainFragment.flFavorites.visibility = View.VISIBLE
                    onFinishRefresh()
                }

                override fun onFailure() {
                    onFinishRefresh()
                }
            })
        }, TAG_FAVORITES).commitAllowingStateLoss()
    }

    private fun initNearBy() {
        rvNearByTab.isNestedScrollingEnabled = false
        val tabs = ArrayList<String>().apply {
            add(context.getString(com.uroad.dubai.R.string.nearMe_roads))
            add(context.getString(com.uroad.dubai.R.string.nearMe_events))
//            add(context.getString(R.string.nearMe_news))
            add(context.getString(com.uroad.dubai.R.string.nearMe_hotel))
            add(context.getString(com.uroad.dubai.R.string.nearMe_restaurants))
            add(context.getString(com.uroad.dubai.R.string.nearMe_attractions))
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
        openLocation()
    }

    override fun afterLocation(location: Location) {
        myLocation = location
        initFragments()
        closeLocation()
    }

    override fun onLocationFailure(exception: Exception) {
        initFragments()
    }

    private fun initFragments() {
        val longitude = myLocation?.longitude ?: 0.0
        val latitude = myLocation?.latitude ?: 0.0
        if (isFirstCommit) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(com.uroad.dubai.R.id.flNearMeRoads, NearMeRoadsFragment.newInstance(longitude, latitude), TAG_ROADS)
            transaction.replace(com.uroad.dubai.R.id.flNearMeEvents, NearMeEventsFragment.newInstance(longitude, latitude), TAG_EVENTS)
//        transaction.replace(R.id.flNearMeNews, NearMeNewsFragment(), TAG_NEWS)
            transaction.replace(com.uroad.dubai.R.id.flNearMeHotel, NearMeHotelFragment.newInstance(longitude, latitude), TAG_HOTEL)
            transaction.replace(com.uroad.dubai.R.id.flNearMeRestaurants, NearMeRestaurantsFragment.newInstance(longitude, latitude), TAG_RESTAURANTS)
            transaction.replace(com.uroad.dubai.R.id.flNearMeAttractions, NearMeAttractionsFragment.newInstance(longitude, latitude), TAG_ATTRACTIONS)
            transaction.commitAllowingStateLoss()
            setCurrentTab(0)
            isFirstCommit = false
        } else {
            refreshNearMe(longitude, latitude)
        }
    }

    private fun setCurrentTab(tab: Int) {
        currentTab = tab
        hideFragments()
        when (tab) {
            0 -> flNearMeRoads.visibility = View.VISIBLE
            1 -> flNearMeEvents.visibility = View.VISIBLE
//            2 -> flNearMeNews.visibility = View.VISIBLE
            2 -> flNearMeHotel.visibility = View.VISIBLE
            3 -> flNearMeRestaurants.visibility = View.VISIBLE
            4 -> flNearMeAttractions.visibility = View.VISIBLE
        }
    }

    private fun hideFragments() {
        flNearMeRoads.visibility = View.GONE
        flNearMeEvents.visibility = View.GONE
        flNearMeNews.visibility = View.GONE
        flNearMeHotel.visibility = View.GONE
        flNearMeRestaurants.visibility = View.GONE
        flNearMeAttractions.visibility = View.GONE
    }

    private fun check(): Boolean {
        if (!UserPreferenceHelper.isLogin(context)) {
            openActivity(LoginActivity::class.java)
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        getUnreadMsg()
    }

    /*获取未读消息*/
    private fun getUnreadMsg() {
        animator?.cancel()
        presenter.messageCenter(WebApi.MESSAGE_CENTER, WebApi.messageCenter("1", getUserId(), 1, 10))
    }

    private fun onRefresh() {
        updateCount = 0
        getUnreadMsg()
        refreshBanner()
        refreshNotice()
        refreshFavorites()
        openLocation()
        refreshLayout.postDelayed({ refreshLayout.finishRefresh() }, 20 * 1000L)
    }

    private fun refreshBanner() {
        val fragment = childFragmentManager.findFragmentByTag(TAG_BANNER)
        if (fragment != null && fragment is MainBannerFragment) fragment.initData()
    }

    private fun refreshNotice() {
        val fragment = childFragmentManager.findFragmentByTag(TAG_NOTICE)
        if (fragment != null && fragment is MainNoticeFragment) fragment.initData()
    }

    private fun refreshFavorites() {
        val fragment = childFragmentManager.findFragmentByTag(TAG_FAVORITES)
        if (fragment != null && fragment is MainFavoritesFragment) fragment.initData()
    }

    private fun refreshNearMe(longitude: Double, latitude: Double) {
        when (currentTab) {
            0 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_ROADS)
                if (fragment != null && fragment is NearMeRoadsFragment) fragment.update(longitude, latitude)
            }
            1 -> {
//                val fragment = childFragmentManager.findFragmentByTag(TAG_EVENTS)
//                if (fragment != null && fragment is NearMeEventsFragment) fragment.initData()
            }
            2 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_HOTEL)
                if (fragment != null && fragment is NearMeHotelFragment) fragment.update(longitude, latitude)
            }
            3 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_RESTAURANTS)
                if (fragment != null && fragment is NearMeRestaurantsFragment) fragment.update(longitude, latitude)
            }
            4 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_ATTRACTIONS)
                if (fragment != null && fragment is NearMeAttractionsFragment) fragment.update(longitude, latitude)
            }
        }
    }

    private fun onFinishRefresh() {
        updateCount++
        if (updateCount >= 3) refreshLayout.finishRefresh()
    }

    override fun onGetNewList(list: MutableList<MessagesMDL>) {
        if (list.isNotEmpty()) {
            animator = AnimUtils.rotate(ivMessage).apply { start() }
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        presenter.detachView()
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}