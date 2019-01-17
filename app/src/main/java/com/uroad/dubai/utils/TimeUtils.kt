package com.uroad.dubai.utils

class TimeUtils {

    companion object {
        fun convertSecond(second: Int): String {
            if (second < 60) return "$second"
            if (second < 3600) {
                val min = second / 60
                val s = second - min * 60
                if (min < 10) {
                    return if (s < 10) {
                        "0$min:0$s"
                    } else "0$min:$s"
                }
                return if (s < 10) {
                    min.toString() + ":0" + s
                } else min.toString() + ":" + s
            }
            val hour = second / 3600
            val minute = (second - hour * 3600) / 60
            val s = second - hour * 3600 - minute * 60
            if (hour < 10) {
                if (minute < 10) {
                    return if (s < 10) {
                        "0$hour:0$minute:0$s"
                    } else "0$hour:0$minute:$s"
                }
                return if (s < 10) {
                    "0$hour$minute:0$s"
                } else "0$hour$minute:$s"
            }
            if (minute < 10) {
                return if (s < 10) {
                    hour.toString() + ":0" + minute + ":0" + s
                } else hour.toString() + ":0" + minute + ":" + s
            }
            return if (second < 10) {
                (hour + minute).toString() + ":0" + s
            } else (hour + minute).toString() + ":" + s
        }

        fun convertSecond2Min(second: Int): String {
            if (second < 60) return "${second}s"
            return "${second / 60}mins"
        }
    }
}