package com.uroad.dubai.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.uroad.dubai.R
import jsc.kit.wheel.base.IWheel
import jsc.kit.wheel.base.WheelView
import java.util.*
import kotlin.collections.ArrayList


class DateTimePickerDialog(private val mContext: Context) : Dialog(mContext, R.style.DefaultDialog) {
    private var currentYear: Int
    private var currentMonth: Int
    private var currentDay: Int
    private var currentHour: Int
    private var currentMinute: Int
    private var maxYear: Int
    private var minYear: Int
    private var yearItems: Array<WheelItem>
    private var monthItems: Array<WheelItem>
    private var dayItems: Array<WheelItem>
    private val hourItems: Array<WheelItem>
    private val minuteItems: Array<WheelItem>
    private var onDateTimePickerListener: OnDateTimePickerListener? = null

    init {
        val calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR) // 获取当前年份
        currentMonth = calendar.get(Calendar.MONTH) + 1// 获取当前月份
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)// 获取当日期
        currentHour = calendar.get(Calendar.HOUR_OF_DAY)//时
        currentMinute = calendar.get(Calendar.MINUTE)//分
        maxYear = currentYear + 50  //往后50年
        minYear = currentYear - 50  //往前50年
        yearItems = getYearItems()
        monthItems = getMonthItems()
        dayItems = getDayItems(getMonthOfDay(currentYear, currentMonth))
        hourItems = getHourItems()
        minuteItems = getMinuteItems()
    }

    private fun getYearItems(): Array<WheelItem> {
        val years = ArrayList<WheelItem>()
        for (i in minYear..maxYear) {
            years.add(WheelItem("$i"))
        }
        return years.toTypedArray()
    }

    private fun getMonthItems() = arrayOf(WheelItem("01"), WheelItem("02"), WheelItem("03"),
            WheelItem("04"), WheelItem("05"), WheelItem("06"), WheelItem("07"), WheelItem("08"),
            WheelItem("09"), WheelItem("10"), WheelItem("11"), WheelItem("12"))

    private fun getMonthOfDay(year: Int, month: Int): Int {
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> return 31
            4, 6, 9, 11 -> return 30
            2 -> return if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                29
            } else {
                28
            }
        }
        return 0
    }

    private fun getDayItems(size: Int): Array<WheelItem> {
        val days = ArrayList<WheelItem>()
        for (i in 1..size) days.add(WheelItem(if (i < 10) "0$i" else "$i"))
        return days.toTypedArray()
    }

    private fun getHourItems(): Array<WheelItem> {
        val hours = ArrayList<WheelItem>()
        for (i in 0..23) hours.add(WheelItem(if (i < 10) "0$i" else "$i"))
        return hours.toTypedArray()
    }

    private fun getMinuteItems(): Array<WheelItem> {
        val minutes = ArrayList<WheelItem>()
        for (i in 0..59) minutes.add(WheelItem(if (i < 10) "0$i" else "$i"))
        return minutes.toTypedArray()
    }

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        window?.let { window ->
            val contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_datetimepicker, LinearLayout(mContext), false)
            val mCancelTv = contentView.findViewById<TextView>(R.id.mCancelTv)
            val mConfirmTv = contentView.findViewById<TextView>(R.id.mConfirmTv)
            val mYearWv = contentView.findViewById<WheelView>(R.id.mYearWv)
            val mMonthWv = contentView.findViewById<WheelView>(R.id.mMonthWv)
            val mDayWv = contentView.findViewById<WheelView>(R.id.mDayWv)
            val mHourWv = contentView.findViewById<WheelView>(R.id.mHourWv)
            val mMinuteWv = contentView.findViewById<WheelView>(R.id.mMinuteWv)
            mYearWv.setItems(yearItems)
            mYearWv.setSelectedIndex(yearItems.indexOf(WheelItem("$currentYear")), false)
            mMonthWv.setItems(monthItems)
            mMonthWv.setSelectedIndex(monthItems.indexOf(WheelItem(if (currentMonth < 10) "0$currentMonth" else "$currentMonth")), false)
            mDayWv.setItems(dayItems)
            mDayWv.setSelectedIndex(dayItems.indexOf(WheelItem(if (currentDay < 10) "0$currentDay" else "$currentDay")), false)
            mHourWv.setItems(hourItems)
            mHourWv.setSelectedIndex(hourItems.indexOf(WheelItem(if (currentHour < 10) "0$currentHour" else "$currentHour")), false)
            mMinuteWv.setItems(minuteItems)
            mMinuteWv.setSelectedIndex(minuteItems.indexOf(WheelItem(if (currentMinute < 10) "0$currentMinute" else "$currentMinute")),false)
            mMonthWv.setOnSelectedListener { _, selectedIndex ->
                val year = yearItems[mYearWv.selectedIndex].showText.toInt()
                val month = monthItems[selectedIndex].showText.toInt()
                dayItems = getDayItems(getMonthOfDay(year, month))
                mDayWv.setItems(dayItems)
            }
            mCancelTv.setOnClickListener { dismiss() }
            mConfirmTv.setOnClickListener {
                val year = yearItems[mYearWv.selectedIndex].showText.toInt()
                val month = monthItems[mMonthWv.selectedIndex].showText.toInt()
                val day = dayItems[mDayWv.selectedIndex].showText.toInt()
                val hour = hourItems[mHourWv.selectedIndex].showText.toInt()
                val minute = minuteItems[mMinuteWv.selectedIndex].showText.toInt()
                onDateTimePickerListener?.onDateTimePicker(this@DateTimePickerDialog, year, month, day, hour, minute)
            }
            window.setContentView(contentView)
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.OperateAnim)
            window.setGravity(Gravity.BOTTOM)
        }
    }

    private class WheelItem(private val text: String) : IWheel {

        override fun getShowText(): String = text

        override fun equals(other: Any?): Boolean {
            return when (other) {
                !is WheelItem -> false
                else -> this === other || text == other.text
            }
        }

        override fun hashCode(): Int {
            return 31 + text.hashCode()
        }
    }

    fun setOnDateTimePickerListener(onDateTimePickerListener: OnDateTimePickerListener): DateTimePickerDialog {
        this.onDateTimePickerListener = onDateTimePickerListener
        return this
    }

    interface OnDateTimePickerListener {
        fun onDateTimePicker(dialog: DateTimePickerDialog, year: Int, month: Int, day: Int, hour: Int, minute: Int)
    }
}