package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.FunctionStatisticsPresenter
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.enumeration.StatisticsType
import kotlinx.android.synthetic.main.activity_more.*

/**
 * @author MFB
 * @create 2018/12/29
 * @describe more function activity
 */
class MoreActivity : BasePresenterActivity<FunctionStatisticsPresenter>() {

    override fun createPresenter(): FunctionStatisticsPresenter = FunctionStatisticsPresenter(this)

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.home_menu_more))
        setBaseContentView(R.layout.activity_more)
        initView()
    }

    private fun initView() {
        tvEvents.setOnClickListener { openEvents() }
        tvHotels.setOnClickListener { openHotels() }
        tvRestaurants.setOnClickListener { openRestaurants() }
        tvAttractions.setOnClickListener { openAttractions() }
        tvBusStop.setOnClickListener { openBusStop() }
        tvParking.setOnClickListener { openParking() }
        tvPolice.setOnClickListener { openPolice() }
        tvGroups.setOnClickListener { openGroups() }
        tvWeather.setOnClickListener { openWeather() }
        tvReport.setOnClickListener { openReport() }
    }

    private fun openEvents() {
        presenter.onFuncStatistics(StatisticsType.TAB_EVENTS.CODE)
        openActivity(EventsListActivity::class.java)
    }

    private fun openHotels() {
        presenter.onFuncStatistics(StatisticsType.TAB_HOTELS.CODE)
        openActivity(AttractionsListActivity::class.java, Bundle().apply { putString("userstatus", NewsType.HOTEL.code) })
    }

    private fun openRestaurants() {
        presenter.onFuncStatistics(StatisticsType.TAB_RESTAURANTS.CODE)
        openActivity(AttractionsListActivity::class.java, Bundle().apply { putString("userstatus", NewsType.RESTAURANT.code) })
    }

    private fun openAttractions() {
        presenter.onFuncStatistics(StatisticsType.TAB_ATTRACTIONS.CODE)
        openActivity(AttractionsListActivity::class.java, Bundle().apply { putString("userstatus", NewsType.ATTRACTION.code) })
    }

    private fun openBusStop() {
        presenter.onFuncStatistics(StatisticsType.BUS_STOPS.CODE)
        openActivity(BusStopListActivity::class.java)
    }

    private fun openParking() {
        presenter.onFuncStatistics(StatisticsType.RTA_PARKING.CODE)
        openActivity(ParkingListActivity::class.java)
    }

    private fun openPolice() {
        presenter.onFuncStatistics(StatisticsType.POLICE.CODE)
        openActivity(PoliceListActivity::class.java)
    }

    private fun openGroups() {
        presenter.onFuncStatistics(StatisticsType.GROUPS.CODE)
        openActivity(GroupsSetupActivity::class.java)
    }

    private fun openWeather() {
        presenter.onFuncStatistics(StatisticsType.WEATHER.CODE)
        openActivity(WeatherActivity::class.java)
    }

    private fun openReport() {
        presenter.onFuncStatistics(StatisticsType.REPORT.CODE)
        openActivity(ReportActivity::class.java)
    }
}