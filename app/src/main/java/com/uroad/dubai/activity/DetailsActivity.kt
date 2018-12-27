package com.uroad.dubai.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : BaseActivity(){

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.details))
        setBaseContentView(R.layout.activity_details)
        val extras = intent.extras
        extras?.let {
            var title = it.getString("title")
            var time = it.getString("time")
            var imgUrl = it.getString("imgUrl")
            var content = it.getString("content")

            tvDetailTitle.text = title
            tvDetailTime.text = time
            tvDetailContent.text = content
            Glide.with(this).load(imgUrl).into(ivDetails)
        }
    }
}