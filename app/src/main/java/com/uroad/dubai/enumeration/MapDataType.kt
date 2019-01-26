package com.uroad.dubai.enumeration

enum class MapDataType(var CODE: String) {
    /**
     * 1010001 事件
    1010002 cctv
    1010003 dms
     */
    EVENT("1010001"),
    ACCIDENT("100001"),
    CONSTRUCTION("100002"),
    PARKING("100003"),
    CCTV("1010002"),
    DMS("1010003"),
    POLICE("100006"),
    WEATHER("100007"),
    RWIS("100008"),
    BUS_STOP("100009"),
    SCENIC("1000010")
}