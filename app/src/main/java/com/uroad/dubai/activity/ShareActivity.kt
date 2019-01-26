package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.common.BaseActivity

class ShareActivity : BaseActivity(){

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle("Share")
        setBaseContentView(R.layout.activity_share)
    }

}