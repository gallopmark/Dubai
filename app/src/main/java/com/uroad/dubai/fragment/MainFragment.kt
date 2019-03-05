package com.uroad.dubai.fragment

import android.animation.ObjectAnimator
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.adapter.NearMeTabAdapter
import com.uroad.dubai.api.presenter.FunctionStatisticsPresenter
import com.uroad.dubai.api.presenter.GroupsPresenter
import com.uroad.dubai.api.presenter.MessagesPresenter
import com.uroad.dubai.api.view.MessagesView
import com.uroad.dubai.common.BaseMapBoxLocationFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.StatisticsType
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.GroupsStateMDL
import com.uroad.dubai.model.MessagesMDL
import com.uroad.dubai.utils.AnimUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.home_content_scroll.*
import kotlinx.android.synthetic.main.home_top_collapse.*
import kotlinx.android.synthetic.main.home_top_expand.*
import kotlinx.android.synthetic.main.home_top_flexhead.*
import java.lang.Exception
import java.util.*


/**
 * @author MFB
 * @create 2018/12/12
 * @describe 首页
 */
class MainFragment : BaseMapBoxLocationFragment(), MessagesView, GroupsPresenter.OnCheckCarStateCallback {
    private var myLocation: Location? = null
    private var currentTab = 0
    private var isOnRefresh = false
    private var updateCount = 0
    private lateinit var presenter: MessagesPresenter
    private lateinit var groupsPresenter: GroupsPresenter
    private lateinit var handler: Handler
    private var animator: ObjectAnimator? = null
    private lateinit var statisticsPresenter: FunctionStatisticsPresenter
//    private var groupsStateMDL: GroupsStateMDL? = null

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
        setContentView(R.layout.fragment_main)
        initLayout()
        initMenu()
        initBanner()
        initNotice()
        initFavorites()
        initNearBy()
        initP()
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
        ivSearch.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivSearchColl.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvNavigation.setOnClickListener { openNavigation() }
        ivNavigation.setOnClickListener { openNavigation() }
        tvHighWay.setOnClickListener { openRoadsList() }
        ivHighWay.setOnClickListener { openRoadsList() }
        tvNews.setOnClickListener { openNewsList() }
        ivNews.setOnClickListener { openNewsList() }
        tvMore.setOnClickListener { openActivity(MoreActivity::class.java) }
        ivMore.setOnClickListener { openActivity(MoreActivity::class.java) }
        ivCenterLogo.setOnClickListener {
//            val content = groupsStateMDL?.content
//            if (!content.isNullOrEmpty()) {
//                openActivity(GroupsDetailActivity::class.java, Bundle().apply { putString("teamId", content[0].teamid) })
//            }
        }
    }

    private fun initP() {
        presenter = MessagesPresenter(this)
        groupsPresenter = GroupsPresenter()
        statisticsPresenter = FunctionStatisticsPresenter(context)
        handler = Handler()
    }

    /**
     * Click Home Navigation (Statistics and Open Navigation Page)
     */
    private fun openNavigation() {
        statisticsPresenter.onFuncStatistics(StatisticsType.HOME_NAVIGATION.CODE)
        openActivity(RoadNavigationActivity::class.java)
    }

    /**
     *Click on Home Roads (Statistics and Open Roadlist  page)
     */
    private fun openRoadsList() {
        statisticsPresenter.onFuncStatistics(StatisticsType.HOME_ROADS.CODE)
        openActivity(RoadsListActivity::class.java)
    }

    /**
     *Click on the home page News (statistics and open News)
     */
    private fun openNewsList() {
        statisticsPresenter.onFuncStatistics(StatisticsType.HOME_NEWS.CODE)
        openActivity(NewsListActivity::class.java)
    }

    private fun initBanner() {
        childFragmentManager.beginTransaction().replace(R.id.flBanner, MainBannerFragment().apply {
            setRequestCallback(object : FragmentRequestCallback {
                override fun onRequestFinish() {
                    onFinishRefresh()
                }
            })
        }, TAG_BANNER).commitAllowingStateLoss()
    }

    private fun initNotice() {
        childFragmentManager.beginTransaction().replace(R.id.flNotice, MainNoticeFragment().apply {
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
        childFragmentManager.beginTransaction().replace(R.id.flFavorites, MainFavoritesFragment().apply {
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
            add(context.getString(R.string.nearMe_roads))
            add(context.getString(R.string.nearMe_events))
//            add(context.getString(R.string.nearMe_news))
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
        openLocation()
    }

    override fun afterLocation(location: Location) {
        myLocation = location
        setCurrentTab(currentTab)
        if (isOnRefresh) refreshNearMe(location.longitude, location.latitude)
        closeLocation()
    }

    override fun onLocationFailure(exception: Exception) {
        setCurrentTab(currentTab)
    }

    private fun setCurrentTab(tab: Int) {
        currentTab = tab
        val myLocation = this.myLocation ?: return
        val longitude = myLocation.longitude
        val latitude = myLocation.latitude
        val transaction = childFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (tab) {
            0 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_ROADS)
                if (fragment != null) transaction.show(fragment)
                else transaction.add(R.id.mNearByFl, NearMeRoadsFragment.newInstance(longitude, latitude), TAG_ROADS)
            }
            1 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_EVENTS)
                if (fragment != null) transaction.show(fragment)
                else transaction.add(R.id.mNearByFl, NearMeEventsFragment.newInstance(longitude, latitude), TAG_EVENTS)
            }
            2 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_HOTEL)
                if (fragment != null) transaction.show(fragment)
                else transaction.add(R.id.mNearByFl, NearMeHotelFragment.newInstance(longitude, latitude), TAG_HOTEL)
            }
            3 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_RESTAURANTS)
                if (fragment != null) transaction.show(fragment)
                else transaction.add(R.id.mNearByFl, NearMeRestaurantsFragment.newInstance(longitude, latitude), TAG_RESTAURANTS)
            }
            else -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_ATTRACTIONS)
                if (fragment != null) transaction.show(fragment)
                else transaction.add(R.id.mNearByFl, NearMeAttractionsFragment.newInstance(longitude, latitude), TAG_ATTRACTIONS)
            }
        }
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        childFragmentManager.findFragmentByTag(TAG_ROADS)?.let { transaction.hide(it) }
        childFragmentManager.findFragmentByTag(TAG_EVENTS)?.let { transaction.hide(it) }
        childFragmentManager.findFragmentByTag(TAG_HOTEL)?.let { transaction.hide(it) }
        childFragmentManager.findFragmentByTag(TAG_RESTAURANTS)?.let { transaction.hide(it) }
        childFragmentManager.findFragmentByTag(TAG_ATTRACTIONS)?.let { transaction.hide(it) }
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
        getGroupsMsg()
    }

    /*获取未读消息*/
    private fun getUnreadMsg() {
        animator?.cancel()
        presenter.messageCenter(WebApi.MESSAGE_CENTER, WebApi.messageCenter("1", getUserUUID(), 1, 10))
    }

    private fun getGroupsMsg() {
        if (!isLogin()) return
        groupsPresenter.checkCarTeam(getUserUUID(), this)
    }

    private fun onRefresh() {
        isOnRefresh = true
        updateCount = 0
        getUnreadMsg()
        refreshBanner()
        refreshNotice()
        refreshFavorites()
        openLocation()
        refreshLayout.postDelayed({ refreshLayout.finishRefresh() }, 30 * 1000L)
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
                val fragment = childFragmentManager.findFragmentByTag(TAG_EVENTS)
                if (fragment != null && fragment is NearMeEventsFragment) fragment.update(longitude, latitude)
            }
            2 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_HOTEL)
                if (fragment != null && fragment is NearMeHotelFragment) fragment.update(longitude, latitude)
            }
            3 -> {
                val fragment = childFragmentManager.findFragmentByTag(TAG_RESTAURANTS)
                if (fragment != null && fragment is NearMeRestaurantsFragment) fragment.update(longitude, latitude)
            }
            else -> {
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

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ getUnreadMsg() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onCheckCarResult(mdl: GroupsStateMDL?) {
//        this.groupsStateMDL = mdl
    }

    override fun onCheckCarFailure(errorMsg: String?) {
        handler.postDelayed({ getGroupsMsg() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        release()
        super.onDestroyView()
    }

    private fun release(){
        presenter.detachView()
        statisticsPresenter.detachView()
        groupsPresenter.detachView()
        handler.removeCallbacksAndMessages(null)
    }
}