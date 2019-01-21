package com.uroad.dubai.webService

class WebApi {
    companion object {
        fun getBaseParams() = HashMap<String, String?>()
        const val GET_NEWS_LIST = "getNewsList"

        fun getNewsListParams(newstype: String?, keyword: String?, index: Int, size: Int) = getBaseParams().apply {
            put("newstype", newstype)
            put("keyword", keyword)
            put("index", index.toString())
            put("size", size.toString())
        }


        const val GET_ROADS_LIST = "getRoadList"

        fun getRoadListParams(index: Int, size: Int, keyword: String) = getBaseParams().apply {
            put("keyword", keyword)
            put("index", index.toString())
            put("size", size.toString())
        }

        const val GET_WEATHER_LIST = "getWeather"
        fun getWeatherListParams() = getBaseParams()

        const val GET_NOTICE = "getNotice"
    }
}