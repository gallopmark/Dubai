package com.uroad.dubai.model

class FavoritesMDL {
    var subtype:String?=null
    var calendarMDL : CalendarListMDL? = null

    fun showTime(weekDataTitle : String) : String{
        val arrayOf = arrayOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")
        val split = weekDataTitle?.split("/")
        split?.let {
            val m : Int = it[0].toInt()
            return "On ${arrayOf[m]} ${it[1]},${it[2]}"
        }
        return ""
    }

}