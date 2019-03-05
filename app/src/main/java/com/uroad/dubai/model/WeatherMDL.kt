package com.uroad.dubai.model

import com.mapbox.mapboxsdk.geometry.LatLng

class WeatherMDL : MapPointItem() {
    var temperature: String? = null
    var weather: String? = null
    var city: String? = null
    var Icon: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    override fun getSmallMarkerIcon(): Int = 0

    override fun getBigMarkerIcon(): Int = 0

    override fun getLatLng(): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        this.latitude?.let { latitude = it }
        this.longitude?.let { longitude = it }
        return LatLng(latitude, longitude)
    }


    //-----------------------------------------------
    var Headline : HeadlineMDL? = null
    var DailyForecasts :  ArrayList<DailyForecastsMDL>? = null

    class HeadlineMDL{
        var EffectiveDate : String? = null
        var EffectiveEpochDate : String? = null
        var Severity : String? = null
        var Text : String? = null
        var Category : String? = null
        var EndDate : String? = null
        var EndEpochDate : String? = null
        var MobileLink : String? = null
        var Link : String? = null
    }

    class DailyForecastsMDL {
        var Date : String? = null
        var EpochDate : String? = null
        var MobileLink : String? = null
        var Link : String? = null
        var Temperature : TemperatureMDL? = null
        var Night : DayOrNight? = null
        var Day : DayOrNight? = null

        class TemperatureMDL{
            var Minimum : MinOrMaximumMDL? = null
            var Maximum : MinOrMaximumMDL? = null
            class MinOrMaximumMDL{
                var Value : Double? = 0.0
                var Unit : String? = null
                var UnitType : String? = null
            }
        }

        class DayOrNight{
            var Icon:String? = null
            var IconPhrase:String? = null
        }

    }
}