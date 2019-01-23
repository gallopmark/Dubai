package com.uroad.dubai.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.adapter.FavoritesAdapter
import com.uroad.dubai.api.presenter.AttractionPresenter
import com.uroad.dubai.api.presenter.CalendarPresenter
import com.uroad.dubai.api.view.AttractionView
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.CalendarMDL
import com.uroad.dubai.model.FavoritesMDL
import com.uroad.dubai.model.ScenicMDL
import com.uroad.library.utils.BitmapUtils
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.travel_content_menu.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 旅游
 */
class TravelFragment : BasePresenterFragment<AttractionPresenter>(), AttractionView, CalendarView {

    private val data = ArrayList<ScenicMDL>()
    private val handler = Handler()
    private lateinit var calendarPresenter: CalendarPresenter
    private val favorites = ArrayList<FavoritesMDL>()

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_travel)
        initAppBar()
        initView()
        //initRv()
        initFavorites()
    }

    override fun createPresenter(): AttractionPresenter = AttractionPresenter(this)
    private fun initAppBar() {
        val statusHeight = DisplayUtils.getStatusHeight(context)
        toolbar.layoutParams = (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply { topMargin = statusHeight }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            if (offset <= total / 2) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        })
        setTopImage()
    }

    private fun initFavorites() {
        bannerFavorites.isNestedScrollingEnabled = false
        bannerFavorites.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        bannerFavorites.adapter = FavoritesAdapter(context, favorites)
        bannerFavorites.initFlingSpeed(9000).initPageParams(-10, 35)
                .initPosition(0)
                .setAnimFactor(0.1f)
                .autoPlay(false)
    }

    //重新计算图片高度 避免图片压缩
    private fun setTopImage() {
        val width = DisplayUtils.getWindowWidth(context)
        val height = (width * 0.67).toInt()
        ivTopPic.layoutParams = ivTopPic.layoutParams.apply {
            this.width = width
            this.height = height
        }
        ivTopPic.scaleType = ImageView.ScaleType.FIT_XY
        ivTopPic.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(resources, R.mipmap.ic_travel_topbg, width, height))
    }

    private fun initView() {
        val bundle = Bundle()
        tvHotels.setOnClickListener {
            bundle.putString("type", NewsType.HOTEL.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvRestaurants.setOnClickListener {
            bundle.putString("type", NewsType.RESTAURANT.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvAttractions.setOnClickListener {
            bundle.putString("type", NewsType.ATTRACTION.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvTransport.setOnClickListener { openActivity(BusStopListActivity::class.java) }
        tvParking.setOnClickListener { openActivity(ParkingListActivity::class.java) }
        tvPolice.setOnClickListener { openActivity(PoliceListActivity::class.java) }
        tvGroups.setOnClickListener { openActivity(GroupsSetupActivity::class.java) }
        tvWeather.setOnClickListener { openActivity(WeatherActivity::class.java) }
        tvMoreClan.setOnClickListener { openActivity(CalendarListActivity::class.java) }

        inspectPermissions()
    }


    override fun initData() {
        //presenter.getAttractions(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4))
    }

    override fun onGetAttraction(attractions: MutableList<ScenicMDL>) {
        val array = arrayOf("1.2km", "1.7km", "2.1km", "5.6km")
        for (i in 0 until attractions.size) {
            if (i < array.size) {
                attractions[i].distance = array[i]
            } else {
                val pos = i % array.size
                attractions[i].distance = array[pos]
            }
        }
        this.data.clear()
        this.data.addAll(attractions)
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed(runnable, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    private val runnable = Runnable { initData() }

    override fun onDestroyView() {
        handler.removeCallbacks(runnable)
        super.onDestroyView()
    }

    override fun loadCalendarSuccess(list: ArrayList<CalendarMDL>) {
        val mdl = list[0]
        mdl.let {
            favorites.clear()
            it.list?.forEach { it1 ->
                val fa = FavoritesMDL()
                fa.calendarMDL = it1
                it1.titleTimeData = it.weekDataTitle?.let { it2 -> fa.showTime(it2) }
                favorites.add(fa)
            }
            bannerFavorites.adapter?.notifyDataSetChanged()
        }
    }

    override fun loadError(e: String) {

    }

    private fun inspectPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                calendarPresenter = CalendarPresenter(this@TravelFragment)
                calendarPresenter.getCalendar(context,true)
            }
        }
    }

}