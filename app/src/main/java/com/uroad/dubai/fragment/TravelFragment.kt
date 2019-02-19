package com.uroad.dubai.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.adapter.TravelCalendarAdapter
import com.uroad.dubai.adaptervp.TravelBannerAdapter
import com.uroad.dubai.adapter.ViewHistoryListCardAdapter
import com.uroad.dubai.adapter.ViewHistoryRoadsListCardAdapter
import com.uroad.dubai.api.presenter.AttractionPresenter
import com.uroad.dubai.api.presenter.BannerPresenter
import com.uroad.dubai.api.presenter.CalendarPresenter
import com.uroad.dubai.api.view.AttractionView
import com.uroad.dubai.api.view.BannerView
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.BannerType
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.CalendarMDL
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.model.ScenicMDL
import com.uroad.dubai.webService.WebApi
import com.uroad.library.decoration.ItemDecoration
import com.uroad.library.utils.DisplayUtils
import com.uroad.library.widget.banner.BaseBannerAdapter
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.item_calendar_norecord.*
import kotlinx.android.synthetic.main.travel_content_menu.*
import android.provider.CalendarContract
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.item_history_norecord.*


/**
 * @author MFB
 * @create 2018/12/12
 * @describe 旅游
 */
class TravelFragment : BasePresenterFragment<AttractionPresenter>(), AttractionView, CalendarView, BannerView {

    private val data = ArrayList<ScenicMDL>()
    private val handler = Handler()
    private var calendarPresenter: CalendarPresenter? = null
    private lateinit var adapter: ViewHistoryListCardAdapter
    private lateinit var adapterRoads: ViewHistoryRoadsListCardAdapter
    private lateinit var bannerPresenter: BannerPresenter
    private lateinit var bannerData: MutableList<NewsMDL>
    private lateinit var bannerAdapter: TravelBannerAdapter
    private lateinit var calendarBanner: TravelCalendarAdapter
    private val calendarList = ArrayList<CalendarMDL>()

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_travel)
        initAppBar()
        initView()
        initBanner()
        initRv()
        initCalendar()
        initRefresh()
    }

    override fun createPresenter(): AttractionPresenter = AttractionPresenter(this)
    private fun initAppBar() {
        val statusHeight = DisplayUtils.getStatusHeight(context)
        toolbar.layoutParams = (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply { topMargin = statusHeight }
        val width = DisplayUtils.getWindowWidth(context)
        val height = (width * 0.6).toInt()
        appBarLayout.layoutParams = appBarLayout.layoutParams.apply {
            this.width = width
            this.height = height
        }
    }

    private fun initRv() {
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ItemDecoration(context, LinearLayoutManager.VERTICAL, DisplayUtils.dip2px(context, 5f), ContextCompat.getColor(context, R.color.white)))
        adapterRoads = ViewHistoryRoadsListCardAdapter(context, data)
        adapter = ViewHistoryListCardAdapter(context, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    openActivity(ScenicDetailActivity::class.java, Bundle().apply { putString("newsId", data[position].newsid) })
                }
            })
        }
        //recyclerView.adapter = adapter
        recyclerView.adapter = adapterRoads
        btnNavigate.setOnClickListener {
            openActivity(RouteNavigationActivity::class.java)
        }
    }

    private fun initBanner() {
        bannerPresenter = BannerPresenter(this)
        bannerData = ArrayList()
        bannerAdapter = TravelBannerAdapter(context, bannerData).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<NewsMDL> {
                override fun onItemClick(t: NewsMDL, position: Int) {
                    if (TextUtils.equals(t.newstype, NewsType.HOTEL.code) ||
                            TextUtils.equals(t.newstype, NewsType.RESTAURANT.code) ||
                            TextUtils.equals(t.newstype, NewsType.ATTRACTION.code)) {
                        openActivity(ScenicDetailActivity::class.java, Bundle().apply { putString("newsId", t.newsid) })
                    } else if (TextUtils.equals(t.newstype, NewsType.NEWS.code)) {
                        openActivity(NewsDetailsActivity::class.java, Bundle().apply {
                            putString("newsId", t.newsid)
                            putString("title", getString(R.string.home_menu_news))
                        })
                    }
                }
            })
        }
        banner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                val realPosition = position % bannerData.size
                collapsingLayout.title = bannerData[realPosition].title
            }
        })
        banner.setAdapter(bannerAdapter)
    }

    private fun initCalendar() {
        calendarBanner = TravelCalendarAdapter(context, calendarList)
        baCalendar.setAdapter(calendarBanner)
        btnAdd.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_INSERT)
                intent.data = CalendarContract.Events.CONTENT_URI
                //intent.putExtra(CalendarContract.Events.TITLE, "")
                intent.action = "android.intent.action.EDIT"
                intent.addCategory ("android.intent.category.DEFAULT")
                intent.type = "vnd.android.cursor.item/event"

                context.startActivityForResult(intent, 100)
            }catch (e : Exception){
                e.printStackTrace()
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun initView() {
        val bundle = Bundle()
        tvHotels.setOnClickListener {
            bundle.putString("userstatus", NewsType.HOTEL.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvRestaurants.setOnClickListener {
            bundle.putString("userstatus", NewsType.RESTAURANT.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvAttractions.setOnClickListener {
            bundle.putString("userstatus", NewsType.ATTRACTION.code)
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

    private fun initRefresh(){
        refreshLayout.setOnRefreshListener { initData() }
    }

    override fun initData() {
        presenter.getAttractions(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 4, 0.0, 0.0))
        bannerPresenter.getBannerNews(BannerType.TRAVEL.CODE)
        loadCalender()
    }

    override fun onGetNews(data: MutableList<NewsMDL>) {
        bannerData.clear()
        bannerData.addAll(data)
        bannerAdapter.notifyDataSetChanged()
        if (bannerData.size > 0) collapsingLayout.title = bannerData[0].title
        refreshLayout.finishRefresh()
    }

    override fun onFailure(errorMsg: String?, errorCode: Int?) {
        handler.postDelayed(runnable, DubaiApplication.DEFAULT_DELAY_MILLIS)
        refreshLayout.finishRefresh()
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
        if(data.size > 0){
            recyclerView.visibility = View.VISIBLE
            ilHistoryNoR.visibility = View.GONE
        }else{
            recyclerView.visibility = View.GONE
            ilHistoryNoR.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        adapterRoads.notifyDataSetChanged()
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed(runnable, DubaiApplication.DEFAULT_DELAY_MILLIS)
        refreshLayout.finishRefresh()
    }

    private val runnable = Runnable { initData() }

    override fun onDestroyView() {
        handler.removeCallbacks(runnable)
        super.onDestroyView()
    }

    override fun loadCalendarSuccess(list: ArrayList<CalendarMDL>) {
        when {
            list.size in 1..3 -> {
                calendarList.clear()
                calendarList.addAll(list)
                baCalendar.visibility = View.VISIBLE
                ilCalendarNoR.visibility = View.GONE
                tvMoreClan.visibility = View.VISIBLE
            }
            list.size > 3 -> {
                calendarList.clear()
                calendarList.add(list[0])
                calendarList.add(list[1])
                calendarList.add(list[2])
                baCalendar.visibility = View.VISIBLE
                ilCalendarNoR.visibility = View.GONE
                tvMoreClan.visibility = View.VISIBLE
            }
            else -> {
                calendarList.clear()
                baCalendar.visibility = View.GONE
                ilCalendarNoR.visibility = View.VISIBLE
                tvMoreClan.visibility = View.INVISIBLE
            }
        }
        baCalendar.getViewPager().adapter = null
        calendarBanner.notifyDataSetChanged()
        baCalendar.setAdapter(calendarBanner)
    }

    override fun loadError(e: String) {
        try {
            calendarBanner.notifyDataSetChanged()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        inspectPermissions()
    }

    private fun inspectPermissions() {
        calendarPresenter = null
        calendarPresenter = CalendarPresenter(this@TravelFragment)
        loadCalender()
    }

    private fun loadCalender() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                calendarPresenter?.getCalendar(context)
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR), 1)
            }
        } else {
            calendarPresenter?.getCalendar(context)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100){
            loadCalender()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty()) {
            var hasPermission = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    hasPermission = false
                    break
                }
            }
            if (hasPermission) {
                calendarPresenter?.getCalendar(context)
            }
        }
    }
}