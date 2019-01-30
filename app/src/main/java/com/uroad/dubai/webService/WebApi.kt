package com.uroad.dubai.webService

class WebApi {
    companion object {
        fun getBaseParams() = HashMap<String, String?>()
        fun simpleParams(userid: String?) = getBaseParams().apply { put("useruuid", userid) }
        const val STARTUP_IMAGE = "getStartUpImage"

        const val APP_VERSION = "getVersion"
        const val GET_NEWS_LIST = "getNewsList"

        fun getNewsListParams(newstype: String?, keyword: String?, index: Int, size: Int,
                              longitude: Double, latitude: Double) = getBaseParams().apply {
            put("newstype", newstype)
            put("keyword", keyword)
            put("index", "$index")
            put("size", "$size")
            put("longitude", "$longitude")
            put("latitude", "$latitude")
        }

        /*新闻详情*/
        const val GET_NEWS_DETAIL = "getNewsDetails"

        fun newsDetailParams(newsId: String?) = getBaseParams().apply { put("newsid", newsId) }

        const val GET_ROADS_LIST = "getRoadList"

        fun getRoadListParams(index: Int, size: Int, keyword: String, longitude: Double, latitude: Double) = getBaseParams().apply {
            put("keyword", keyword)
            put("index", "$index")
            put("size", "$size")
            put("longitude", "$longitude")
            put("latitude", "$latitude")
        }

        const val GET_WEATHER_LIST = "getWeather"
        fun getWeatherListParams() = getBaseParams()

        const val GET_NOTICE = "getNotice"

        /*订阅路线*/
        const val SUBSCRIBE_ROUTE = "subscribeRoute"

        fun subscribeRouteParams(useruuid: String?, startpoint: String?, endpoint: String?,
                                 startlatitudeandlongitude: String?, endlatitudeandlongitude: String?,
                                 plancode: String?, routeid: String?, coordinates: String?) = getBaseParams().apply {
            put("useruuid", useruuid)
            put("startpoint", startpoint)
            put("endpoint", endpoint)
            put("startlatitudeandlongitude", startlatitudeandlongitude)
            put("endlatitudeandlongitude", endlatitudeandlongitude)
            put("plancode", plancode)
            put("routeid", routeid)
            put("coordinates", coordinates)
        }

        /*取消订阅*/
        const val UNSUBSCRIBE_ROUTE = "unsubscribe"

        fun unSubscribeRouteParams(useruuid: String?, routeId: String?, type: String?) = getBaseParams().apply {
            put("useruuid", useruuid)
            put("dataid", routeId)
            put("type", type)
        }

        /*获取订阅数据*/
        const val GET_SUBSCRIBE_DATA = "getSubscribeData"

        /*轮播图新闻*/
        const val BANNER_NEWS = "getBannerNews"

        fun bannerNewsParams(bannertype: String?) = getBaseParams().apply { put("bannertype", bannertype) }

        /*设置个人地址*/
        const val SETUP_USER_ADDRESS = "setUpUserAddress"

        fun setUpUserAddressParams(addressid: String?, useruuid: String?, address: String?
                                   , lnglat: String?, addresstype: String?) = getBaseParams().apply {
            put("addressid", addressid)
            put("useruuid", useruuid)
            put("address", address)
            put("lnglat", lnglat)
            put("addresstype", addresstype)
        }

        /*获取地址*/
        const val GET_USER_ADDRESS = "getUserAddress"

        fun getUserAddressParams(useruuid: String?, size: String?) = getBaseParams().apply {
            put("useruuid", useruuid)
            put("size", size.toString())
        }

        /*删除地址*/
        const val DELETE_USER_ADDRESS = "deleteUserAddress"

        fun deleteUserAddressParams(addressid: String?) = getBaseParams().apply { put("addressid", addressid) }


        const val USER_LOGIN = "login"
        fun login(logintype: String, phone: String, password: String) = getBaseParams().apply {
            put("logintype", logintype)
            put("phone", phone)
            put("password", password)
        }

        const val SEND_VERIFICATION_CODE = "sendVerificationCode"
        fun sendVerificationCode(phone: String) = getBaseParams().apply {
            put("phone", phone)
        }

        /*地图插点*/
        const val MAP_POINT = "mapPoints"

        fun mapPointParams(type: String?, useruuid: String?, longitude: Double, latitude: Double) = getBaseParams().apply {
            put("type", type)
            put("useruuid", useruuid)
            put("longitude", "$longitude")
            put("latitude", "$latitude")
        }

        const val FORGOT_PASSWORD = "forgotPassword"
        fun forgotPassword(phone: String, verificationcode: String, password: String) = getBaseParams().apply {
            put("phone", phone)
            put("verificationcode", verificationcode)
            put("password", password)
        }

        const val SAVEFEEDBACK = "saveFeedBack"
        fun saveFeedBack(useruuid: String, nickname: String, rating: String, feedbackcontent: String, phone: String) = getBaseParams().apply {
            put("useruuid", useruuid)
            put("nickname", nickname)
            put("rating", rating)
            put("feedbackcontent", feedbackcontent)
            put("phone", phone)
        }

        const val MESSAGE_CENTER = "messageCenter"
        fun messageCenter(type: String?, useruuid: String?, index: Int, size: Int) = getBaseParams().apply {
            put("type", type)
            put("useruuid", useruuid)
            put("index", "$index")
            put("size", "$size")
        }

        const val MESSAGE_DETAIL = "getMessageDetails"

        fun messageDetailParams(messageid: String?) = getBaseParams().apply { put("messageid", messageid) }

        const val EVENT_LIST = "getEventList"

        fun eventListParams(index: Int, size: Int, roadid: String?, longitude: Double, latitude: Double) = getBaseParams().apply {
            if (index == 0) put("index", "")
            else put("index", "$index")
            if (size == 0) put("size", "")
            else put("size", "$size")
            put("roadid", roadid)
            if (longitude > 0) put("longitude", "$longitude")
            else put("longitude", "")
            if (latitude > 0) put("latitude", "$latitude")
            else put("latitude", "")
        }

        const val EVENT_DETAILS = "getEventDetails"

        fun eventDetailsParams(eventid: String?, useruuid: String?) = getBaseParams().apply {
            put("eventid", eventid)
            put("useruuid", useruuid)
        }

        const val VALIDATIONDEVICE = "validationDevice"
        fun validationDevice(deviceid: String) = getBaseParams().apply {
            put("deviceid", deviceid)
        }
    }
}