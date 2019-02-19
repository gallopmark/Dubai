package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.library.common.BaseActivity
import com.uroad.dubai.dialog.DateTimePickerDialog
import kotlinx.android.synthetic.main.activity_reportsubmit.*

class ReportSubmitActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_reportsubmit)
        withTitle(getString(R.string.report_submit))
        initView()
    }

    private fun initView() {
        mStartTimeLl.setOnClickListener { showDateTimePickerDialog(1) }
        mEndTimeLl.setOnClickListener { showDateTimePickerDialog(2) }
    }

    private fun showDateTimePickerDialog(type: Int) {
        DateTimePickerDialog(this).setOnDateTimePickerListener(object : DateTimePickerDialog.OnDateTimePickerListener {
            override fun onDateTimePicker(dialog: DateTimePickerDialog, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
                dialog.dismiss()
                val text = "${if (day < 10) "0$day" else "$day"}/${if (month < 10) "0$month" else "$month"}/$year\u2000${if (hour < 10) "0$hour" else "$hour"}:${if (minute < 10) "0$minute" else "$minute"}"
                if (type == 1) {
                    mStartTimeTv.text = text
                } else {
                    mEndTimeTv.text = text
                }
            }
        }).show()
    }
}