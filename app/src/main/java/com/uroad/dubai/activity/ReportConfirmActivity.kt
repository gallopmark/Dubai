package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity

class ReportConfirmActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_reportconfirm)
        withTitle(getString(R.string.report_final_confirmation))
    }
}