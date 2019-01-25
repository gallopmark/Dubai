package com.uroad.dubai.webService

class WebApi {
    companion object {
        fun getBaseParams() = HashMap<String, String?>()
        fun simpleParams(userid: String?) = getBaseParams().apply { put("useruuid", userid) }
        const val GET_NEWS_LIST = "getNewsList"

        fun getNewsListParams(newstype: String?, keyword: String?, index: Int, size: Int) = getBaseParams().apply {
            put("newstype", newstype)
            put("keyword", keyword)
            put("index", index.toString())
            put("size", size.toString())
        }

        /*新闻详情*/
        const val GET_NEWS_DETAIL = "getNewsDetails"

        fun newsDetailParams(newsId: String?) = getBaseParams().apply { put("newsid", newsId) }

        const val GET_ROADS_LIST = "getRoadList"

        fun getRoadListParams(index: Int, size: Int, keyword: String) = getBaseParams().apply {
            put("keyword", keyword)
            put("index", index.toString())
            put("size", size.toString())
        }

        const val GET_WEATHER_LIST = "getWeather"
        fun getWeatherListParams() = getBaseParams()

        const val GET_NOTICE = "getNotice"

        /*订阅路线*/
        const val SUBSCRIBE_ROUTE = "subscribeRoute"

        fun subscribeRouteParams(userid: String?, startpoint: String?, endpoint: String?,
                                 startlatitudeandlongitude: String?, endlatitudeandlongitude: String?,
                                 plancode: String?, routeid: String?, coordinates: String?) = getBaseParams().apply {
            put("useruuid", userid)
            put("startpoint", startpoint)
            put("endpoint", endpoint)
            put("startlatitudeandlongitude", startlatitudeandlongitude)
            put("endlatitudeandlongitude", endlatitudeandlongitude)
            put("plancode", plancode)
            put("routeid", routeid)
            put("coordinates", coordinates)
        }

        /*获取订阅数据*/
        const val GET_SUBSCRIBE_DATA = "getSubscribeData"

        /*轮播图新闻*/
        const val BANNER_NEWS = "getBannerNews"

        fun bannerNewsParams(bannertype: String?) = getBaseParams().apply { put("bannertype", bannertype) }


        const val USER_LOGIN = "login"
        fun login(logintype : String,phone : String,password : String,appversion : String,
                  deviceid : String,devicename : String,devicetype : String,devicesysversion : String) = getBaseParams().apply {
            put("logintype",logintype)
            put("phone",phone)
            put("password",password)
            put("appversion",appversion)
            put("deviceid",deviceid)
            put("devicename",devicename)
            put("devicetype",devicetype)
            put("devicesysversion",devicesysversion)
        }

        const val SEND_VERIFICATION_CODE = "sendVerificationCode"
        fun sendVerificationCode(phone : String) = getBaseParams().apply {
            put("phone",phone)
        }
    }
}