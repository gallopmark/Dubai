package com.uroad.dubai.api.presenter

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.uroad.dubai.R
import com.uroad.dubai.activity.ScenicDetailActivity
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.RoadNavigationView
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.dialog.*
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.*
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.utils.SymbolGenerator
import com.uroad.dubai.webService.WebApi
import io.reactivex.disposables.Disposable

class RoadNavigationPresenter(private val activity: BaseActivity,
                              private val navigationView: RoadNavigationView)
    : BasePresenter<RoadNavigationView>(navigationView) {
    private var disposable: Disposable? = null
    private val latitudeArray = doubleArrayOf(24.29045862222854, 25.71109733694287, 25.50251457879257,
            25.260430807520947, 24.98663128116354, 24.861623304922887, 24.583673073761176, 24.65789600887483,
            25.035503774803118, 25.012879832914024, 24.604357106770124, 24.628219646337257, 25.235800209105093,
            24.4970057134079, 25.151713168516054, 5.47217244601599, 24.919234574159304, 24.218204151399064,
            24.18581475318689, 24.20949902407014, 25.303561875079183, 25.473265821079735, 24.423435007145386,
            25.115663223796332, 25.0828064, 25.2494113, 25.1997786)
    private val longitudeArray = doubleArrayOf(55.852681624449815, 55.984812838019934, 56.223575439742945,
            56.18408021591233, 56.13103272127739, 56.069466637252845, 55.74578456977292, 55.69731953832229,
            55.59616854894591, 55.28375285528648, 55.13522740529925, 54.74918917311584, 55.32494669342714,
            55.43882546263342, 55.8957487204126, 56.009015219291996, 56.11049688466619, 55.5668175990312,
            55.26069186566269, 54.41366434182248, 55.4707649820881, 55.58799714838494, 55.02788501046564,
            55.19529231879494, 55.138188600000035, 55.35392000000005, 55.27739859999998)

    fun get3DBuildingLayer(): FillExtrusionLayer = FillExtrusionLayer("3d-buildings", "composite").apply {
        sourceLayer = "building"
        setFilter(Expression.eq(Expression.get("extrude"), "true"))
        minZoom = 15f
        setProperties(PropertyFactory.fillExtrusionColor(Color.LTGRAY),
                PropertyFactory.fillExtrusionHeight(Expression.interpolate(Expression.exponential(1f),
                        Expression.zoom(),
                        Expression.stop(15, Expression.literal(0)),
                        Expression.stop(16, Expression.get("height")))),
                PropertyFactory.fillExtrusionBase(Expression.get("min_height")),
                PropertyFactory.fillExtrusionOpacity(0.9f)
        )
    }

    fun getScenic() {
        disposable?.dispose()
        disposable = request(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.ATTRACTION.code, "", 1, 10), object : StringObserver(navigationView) {
            override fun onHttpResultOk(data: String?) {
                navigationView.onGetScenic(GsonUtils.fromDataToList(data, ScenicMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                navigationView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }

    fun getMapPointByType(code: String) {
        when (code) {
            MapDataType.ACCIDENT.CODE, MapDataType.CONSTRUCTION.CODE -> navigationView.onGetMapPoi(code, ArrayList<MapPointItem>().apply {
                addAll(getAccident())
                addAll(getConstruction())
            })
            MapDataType.PARKING.CODE -> navigationView.onGetMapPoi(code, getParking())
            MapDataType.CCTV.CODE -> navigationView.onGetMapPoi(code, getCCTV())
            MapDataType.DMS.CODE -> navigationView.onGetMapPoi(code, getDMS())
            MapDataType.POLICE.CODE -> navigationView.onGetMapPoi(code, getPolice())
            MapDataType.WEATHER.CODE -> navigationView.onGetMapPoi(code, getWeather())
            MapDataType.RWIS.CODE -> navigationView.onGetMapPoi(code, getRWIS())
            MapDataType.BUS_STOP.CODE -> navigationView.onGetMapPoi(code, getBusStop())
        }
    }

    private fun getAccident(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(EventsMDL().apply {
            eventtypename = "Accident"
            updatetime = "a min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
            latitude = latitudeArray[0]
            longitude = longitudeArray[0]
            subtype = MapDataType.ACCIDENT.CODE
            occtime = "14:23"
            handletime = "14:32"
            realovertime = "16:42"
        })
        add(EventsMDL().apply {
            eventtypename = "Accident"
            updatetime = "a min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
            latitude = latitudeArray[1]
            longitude = longitudeArray[1]
            subtype = MapDataType.ACCIDENT.CODE
            occtime = "14:23"
            handletime = "14:32"
            realovertime = "16:42"
        })
        add(EventsMDL().apply {
            eventtypename = "Accident"
            updatetime = "a min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
            latitude = latitudeArray[2]
            longitude = longitudeArray[2]
            subtype = MapDataType.ACCIDENT.CODE
            occtime = "14:23"
            handletime = "14:32"
            realovertime = "16:42"
        })
    }

    private fun getConstruction(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(EventsMDL().apply {
            eventtypename = "Roadwork"
            updatetime = "3 min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "Construction is under way following a series of car collisions and damage to guardrails at the junction of Road E611 in the direction of the Road Maleha in the direction of Emirates Rd on S116 Road"
            latitude = latitudeArray[3]
            longitude = longitudeArray[3]
            subtype = MapDataType.CONSTRUCTION.CODE
            occtime = "14:23"
            realovertime = "16:42"
        })
        add(EventsMDL().apply {
            eventtypename = "Accident"
            updatetime = "a min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
            latitude = latitudeArray[4]
            longitude = longitudeArray[4]
            subtype = MapDataType.CONSTRUCTION.CODE
            occtime = "14:23"
            realovertime = "16:42"
        })
        add(EventsMDL().apply {
            eventtypename = "Accident"
            updatetime = "a min ago"
            roadtitle = "Dubai to ABU dhabi"
            statusname = "Jammed"
            reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
            latitude = latitudeArray[5]
            longitude = longitudeArray[5]
            subtype = MapDataType.CONSTRUCTION.CODE
            occtime = "14:23"
            realovertime = "16:42"
        })
    }

    private fun getParking(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(ParkingMDL().apply {
            title = "Parking at the shopping center  in dubai"
            content = "Financial Center Rd – Dubai "
            latitude = latitudeArray[6]
            longitude = longitudeArray[6]
            available = "5"
            total = "40"
        })
        add(ParkingMDL().apply {
            title = "Parking at the shopping center  in dubai"
            content = "Financial Center Rd – Dubai "
            latitude = latitudeArray[7]
            longitude = longitudeArray[7]
            available = "5"
            total = "40"
        })
        add(ParkingMDL().apply {
            title = "Parking at the shopping center  in dubai"
            content = "Financial Center Rd – Dubai "
            latitude = latitudeArray[8]
            longitude = longitudeArray[8]
            available = "5"
            total = "40"
        })
    }

    private fun getCCTV(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(CCTVSnapMDL().apply {
            shortname = "CCTV"
            resname = "Dubai to ABU dhabi"
            latitude = latitudeArray[9]
            longitude = longitudeArray[9]
        })
        add(CCTVSnapMDL().apply {
            shortname = "CCTV"
            resname = "Dubai to ABU dhabi"
            latitude = latitudeArray[10]
            longitude = longitudeArray[10]
        })
        add(CCTVSnapMDL().apply {
            shortname = "CCTV"
            resname = "Dubai to ABU dhabi"
            latitude = latitudeArray[11]
            longitude = longitudeArray[11]
        })
    }

    private fun getDMS(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(DMSysMDL().apply {
            title = "DMS"
            content = "Dubai to ABU dhabi"
            latitude = latitudeArray[12]
            longitude = longitudeArray[12]
        })
        add(DMSysMDL().apply {
            title = "DMS"
            content = "Dubai to ABU dhabi"
            latitude = latitudeArray[13]
            longitude = longitudeArray[13]
        })
        add(DMSysMDL().apply {
            title = "DMS"
            content = "Dubai to ABU dhabi"
            latitude = latitudeArray[14]
            longitude = longitudeArray[14]
        })
    }

    private fun getPolice(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(PoliceMDL().apply {
            title = "DP - Ports Police Station"
            content = "Near to DDC Jumeirah, Jumeirah Road - Dubai Faqea Police Station"
            telphone = "04-9784211"
            latitude = latitudeArray[15]
            longitude = longitudeArray[15]
        })
        add(PoliceMDL().apply {
            title = "DP - Ports Police Station"
            content = "Near to DDC Jumeirah, Jumeirah Road - Dubai Faqea Police Station"
            telphone = "04-9784211"
            latitude = latitudeArray[16]
            longitude = longitudeArray[16]
        })
        add(PoliceMDL().apply {
            title = "DP - Ports Police Station"
            content = "Near to DDC Jumeirah, Jumeirah Road - Dubai Faqea Police Station"
            telphone = "04-9784211"
            latitude = latitudeArray[17]
            longitude = longitudeArray[17]
        })
    }

    private fun getWeather(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(WeatherMDL().apply {
            city = "Deira"
            temperature = "23°"
            latitude = latitudeArray[18]
            longitude = longitudeArray[18]
        })
        add(WeatherMDL().apply {
            city = "Deira"
            temperature = "23°"
            latitude = latitudeArray[19]
            longitude = longitudeArray[19]
        })
        add(WeatherMDL().apply {
            city = "Deira"
            temperature = "23°"
            latitude = latitudeArray[20]
            longitude = longitudeArray[20]
        })
    }

    private fun getRWIS(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(RWISMDL().apply {
            title = "A1233445"
            content = "E11(Sheikh Zayed Road)"
            reporttime = "12/12/2018 14:23"
            windspeed = "3MP/h"
            visibility = "10 mile(s);0"
            skyconditions = "mostly cloudy"
            temperature = "36℃"
            dewpoint = "32℃"
            relativehumidity = "76%"
            latitude = latitudeArray[21]
            longitude = longitudeArray[21]
        })
        add(RWISMDL().apply {
            title = "A1233445"
            content = "E11(Sheikh Zayed Road)"
            reporttime = "12/12/2018 14:23"
            windspeed = "3MP/h"
            visibility = "10 mile(s);0"
            skyconditions = "mostly cloudy"
            temperature = "36℃"
            dewpoint = "32℃"
            relativehumidity = "76%"
            latitude = latitudeArray[22]
            longitude = longitudeArray[22]
        })
        add(RWISMDL().apply {
            title = "A1233445"
            content = "E11(Sheikh Zayed Road)"
            reporttime = "12/12/2018 14:23"
            windspeed = "3MP/h"
            visibility = "10 mile(s);0"
            skyconditions = "mostly cloudy"
            temperature = "36℃"
            dewpoint = "32℃"
            relativehumidity = "76%"
            latitude = latitudeArray[23]
            longitude = longitudeArray[23]
        })
    }

    private fun getBusStop(): MutableList<MapPointItem> = ArrayList<MapPointItem>().apply {
        add(BusStopMDL().apply {
            title = "AI Qusais"
            content = "Near Dawar Trading Center - Sheikh Khalifa Bin  Zayed St - Duba"
            latitude = latitudeArray[24]
            longitude = longitudeArray[24]
        })
        add(BusStopMDL().apply {
            title = "AI Qusais"
            content = "Near Dawar Trading Center - Sheikh Khalifa Bin  Zayed St - Duba"
            latitude = latitudeArray[25]
            longitude = longitudeArray[25]
        })
        add(BusStopMDL().apply {
            title = "AI Qusais"
            content = "Near Dawar Trading Center - Sheikh Khalifa Bin  Zayed St - Duba"
            latitude = latitudeArray[26]
            longitude = longitudeArray[26]
        })
    }

    fun generateWeather(item: WeatherMDL): Bitmap {
        val view = LayoutInflater.from(activity).inflate(R.layout.content_map_weather, LinearLayout(activity), false)
        val tvCity = view.findViewById<TextView>(R.id.tvCity)
        val tvTemperature = view.findViewById<TextView>(R.id.tvTemperature)
        val ivWeather = view.findViewById<ImageView>(R.id.ivWeather)
        tvCity.text = item.city
        tvTemperature.text = item.temperature
        ivWeather.setImageResource(R.mipmap.ic_weather_sun)
        return SymbolGenerator.generate(view)
    }

    fun onMarkerClick(marker: Marker, item: MapPointItem) {
        marker.icon = IconFactory.getInstance(activity).fromResource(item.getBigMarkerIcon())
        if (item is ScenicMDL) {
            DubaiApplication.clickItemScenic = item
            activity.openActivity(ScenicDetailActivity::class.java)
            marker.icon = IconFactory.getInstance(activity).fromResource(item.getSmallMarkerIcon())
        } else {
            var dialog: Dialog? = null
            when (item) {
                is EventsMDL -> dialog = EventsDetailDialog(activity, item)
                is ParkingMDL -> {
                    dialog = ParkingDetailDialog(activity, item).setOnNavigateListener(object : ParkingDetailDialog.OnNavigateListener {
                        override fun onNavigate(mdl: ParkingMDL, dialog: ParkingDetailDialog) {

                        }
                    })
                }
                is CCTVSnapMDL -> dialog = CCTVSnapDetailDialog(activity, item)
                is DMSysMDL -> dialog = DMSDetailDialog(activity, item)
                is PoliceMDL -> {
                    dialog = PoliceDetailDialog(activity, item).setOnNavigateListener(object : PoliceDetailDialog.OnNavigateListener {
                        override fun onNavigate(mdl: PoliceMDL, dialog: PoliceDetailDialog) {

                        }
                    })
                }
                is RWISMDL -> dialog = RWISDetailDialog(activity, item)
                is BusStopMDL -> {
                    dialog = BusStopDetailDialog(activity, item).setOnNavigateListener(object : BusStopDetailDialog.OnNavigateListener {
                        override fun onNavigate(mdl: BusStopMDL, dialog: BusStopDetailDialog) {

                        }
                    })
                }
            }
            dialog?.show()
            dialog?.setOnDismissListener { marker.icon = IconFactory.getInstance(activity).fromResource(item.getSmallMarkerIcon()) }
        }
    }
}