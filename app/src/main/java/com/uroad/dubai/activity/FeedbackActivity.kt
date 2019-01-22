package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity

class FeedbackActivity : BaseActivity() {

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.mine_feedback))
        setBaseContentView(R.layout.activity_feed_back)
    }

}