package com.uroad.dubai.api.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import com.uroad.dubai.api.view.CalendarView
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.model.CalendarListMDL
import com.uroad.dubai.model.CalendarMDL
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class CalendarPresenter(val calendarView: CalendarView) : BasePresenter<CalendarView>(calendarView) {

    //private var CALENDER_URL: String = "content://com.android.calendar/calendars"
    private var CALENDER_EVENT_URL: String = "content://com.android.calendar/events"
    //private var CALENDER_REMINDER_URL: String = "content://com.android.calendar/reminders"

    @SuppressLint("CheckResult")
    fun getCalendar(context: Context) {
        addDisposable(Observable.fromCallable { getCalendar2(context) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ calendarView.loadCalendarSuccess(it)
                }, { calendarView.loadError(it.toString()) }))
    }

    private fun getCalendar2(context: Context): ArrayList<CalendarMDL> {
        val list = ArrayList<CalendarMDL>()
        val cStart = getPeriodDate(0)
        val cEnd = getPeriodDate(1)
        var theLast = ""
        var last = CalendarMDL()
        var lastList = ArrayList<CalendarListMDL>()

        val selection = "((dtstart >= " + cStart.timeInMillis + ") AND (dtend <= " + cEnd.timeInMillis + "))"
        val eventCursor = context.contentResolver.query(Uri.parse(CALENDER_EVENT_URL), null,
                selection, null, "${CalendarContract.Events.DTSTART} DESC")
        eventCursor?.let {
            while (it.moveToNext()) {
                CalendarContract.ACCOUNT_TYPE_LOCAL
                //事件的ID
                //val id = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
                //事件的标题
                val title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
                //事件的起始时间
                val dtstart = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                //事件的结束时间 ，如果事件是每天/周,那么就没有结束时间
                val dtend = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))
                //事件的描述
                val description = it.getString(it.getColumnIndex(CalendarContract.Events.DESCRIPTION))
                //事件的重复规律
                val rrule = it.getString(it.getColumnIndex(CalendarContract.Events.RRULE))
                //事件的复发日期。通常RDATE要联合RRULE一起使用来定义一个重复发生的事件的合集。
                val rdate = it.getString(it.getColumnIndex(CalendarContract.Events.RDATE))
                //事件是否是全天的
                val allDay = it.getString(it.getColumnIndex(CalendarContract.Events.ALL_DAY))
                //事件的地点
                val eventLocation = it.getString(it.getColumnIndex(CalendarContract.Events.EVENT_LOCATION))
                //事件持续时间，例如“PT1H”表示事件持续1小时的状态， “P2W”指明2周的持续时间。P3600S表示3600秒
                val duration = it.getString(it.getColumnIndex(CalendarContract.Events.DURATION))
                //事件组织者（所有者）的电子邮件。
                val organizer = it.getString(it.getColumnIndex(CalendarContract.Events.ORGANIZER))

                val current = getWeekOfDate(timeStamp2Date(dtstart).substring(0, 10))
                var mdl: CalendarMDL?
                var currentList: ArrayList<CalendarListMDL>? = null

                if (TextUtils.equals(theLast, current)) {
                    mdl = last
                    currentList = lastList
                } else {
                    currentList = ArrayList()
                    mdl = CalendarMDL()
                    last = mdl
                    lastList = currentList
                    mdl.weekDataTitle = current
                    theLast = current
                }

                val json = CalendarListMDL()
                json.allDay = allDay
                json.eventLocation = eventLocation
                json.title = title
                json.dtstart = timeStamp2Date(dtstart)
                json.dtend = timeStamp2Date(dtend)
                json.description = description
                json.rrule = rrule
                json.rdate = rdate
                json.duration = duration
                json.weekDataTitle = current
                json.organizer = organizer

                list.remove(mdl)
                currentList.add(json)
                mdl.list = currentList
                list.add(mdl)
            }
            it.close()
        }

        return list
    }


    private fun timeStamp2Date(time: Long): String {
        return try {
            val format = "yyyy-MM-dd HH:mm:ss"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.format(Date(time))
        } catch (e: Exception) {
            ""
        }
    }

    private fun getPeriodDate(mode: Int): Calendar {
        val c = Calendar.getInstance()
        val day = when (mode) {
            0 -> c.get(Calendar.DAY_OF_MONTH) - 30 //一个月前
            else -> c.get(Calendar.DAY_OF_MONTH) + 30 // 一个月后
        }
        c.set(Calendar.DAY_OF_MONTH, day)
        return c
    }

    //	实现给定某日期，判断是星期几
    /**
     * @param date
     * @return 当前日期是星期几
     */
    private fun getWeekOfDate(date: String): String {
        val weekDays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val cal = Calendar.getInstance()
        cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
        var w = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0)
            w = 0
        val split = date.split("-")
        return "${split[1]}/${split[2]}/${weekDays[w]}"
    }
}