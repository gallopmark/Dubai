package com.uroad.dubai.local

import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.model.EventsMDL

class DataSource {
    class MapData {
        companion object {
            private val latitudeArray = doubleArrayOf(24.29045862222854, 25.71109733694287, 25.50251457879257,
                    25.260430807520947, 24.98663128116354, 24.861623304922887, 24.583673073761176, 24.65789600887483,
                    25.035503774803118, 25.012879832914024, 24.604357106770124, 24.628219646337257, 25.235800209105093,
                    24.4970057134079, 25.151713168516054, 5.47217244601599, 24.919234574159304, 24.218204151399064,
                    24.18581475318689, 24.20949902407014, 25.303561875079183, 25.473265821079735, 24.423435007145386, 25.115663223796332)
            private val longitudeArray = doubleArrayOf(55.852681624449815, 55.984812838019934, 56.223575439742945,
                    56.18408021591233, 56.13103272127739, 56.069466637252845, 55.74578456977292, 55.69731953832229,
                    55.59616854894591, 55.28375285528648, 55.13522740529925, 54.74918917311584, 55.32494669342714,
                    55.43882546263342, 55.8957487204126, 56.009015219291996, 56.11049688466619, 55.5668175990312,
                    55.26069186566269, 54.41366434182248, 55.4707649820881, 55.58799714838494, 55.02788501046564, 55.19529231879494)

            fun getAccident(): MutableList<EventsMDL> = ArrayList<EventsMDL>().apply {
                add(EventsMDL().apply {
                    eventtypename = "Accident"
                    updatetime = "a min ago"
                    roadtitle = "Dubai to ABU dhabi"
                    statusname = "Jammed"
                    reportout = "On the morning of 6 February, 22 people were injured in  a major road accident along sheikh Mohammed bin rashid al-maktoum road (E311) from dubai to ABU dhabi."
                    latitude = latitudeArray[0]
                    longitude = longitudeArray[0]
                    subtype = MapDataType.ACCIDENT.code
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
                    subtype = MapDataType.ACCIDENT.code
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
                    subtype = MapDataType.ACCIDENT.code
                    occtime = "14:23"
                    handletime = "14:32"
                    realovertime = "16:42"
                })
            }

            fun getConstruction(): MutableList<EventsMDL> = ArrayList<EventsMDL>().apply {
                add(EventsMDL().apply {
                    eventtypename = "Roadwork"
                    updatetime = "3 min ago"
                    roadtitle = "Dubai to ABU dhabi"
                    statusname = "Jammed"
                    reportout = "Construction is under way following a series of car collisions and damage to guardrails at the junction of Road E611 in the direction of the Road Maleha in the direction of Emirates Rd on S116 Road"
                    latitude = latitudeArray[3]
                    longitude = longitudeArray[3]
                    subtype = MapDataType.CONSTRUCTION.code
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
                    subtype = MapDataType.CONSTRUCTION.code
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
                    subtype = MapDataType.CONSTRUCTION.code
                    occtime = "14:23"
                    realovertime = "16:42"
                })
            }

            fun getParking(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[6], longitudeArray[6]))
                add(LatLng(latitudeArray[7], longitudeArray[7]))
                add(LatLng(latitudeArray[8], longitudeArray[8]))
            }

            fun getCCTV(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[9], longitudeArray[8]))
                add(LatLng(latitudeArray[10], longitudeArray[10]))
                add(LatLng(latitudeArray[11], longitudeArray[11]))
            }

            fun getDMS(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[12], longitudeArray[12]))
                add(LatLng(latitudeArray[13], longitudeArray[13]))
                add(LatLng(latitudeArray[14], longitudeArray[14]))
            }

            fun getPolice(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[15], longitudeArray[15]))
                add(LatLng(latitudeArray[16], longitudeArray[16]))
                add(LatLng(latitudeArray[17], longitudeArray[17]))
            }

            fun getWeather(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[18], longitudeArray[18]))
                add(LatLng(latitudeArray[19], longitudeArray[19]))
                add(LatLng(latitudeArray[20], longitudeArray[20]))
            }

            fun getRWIS(): MutableList<LatLng> = ArrayList<LatLng>().apply {
                add(LatLng(latitudeArray[21], longitudeArray[21]))
                add(LatLng(latitudeArray[22], longitudeArray[21]))
                add(LatLng(latitudeArray[23], longitudeArray[21]))
            }
        }
    }
}