package com.uroad.dubai.activity

import android.os.Bundle
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.glidev4.GlideV4
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : BaseActivity(){

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.details))
        setBaseContentView(R.layout.activity_details)
        val extras = intent.extras
        extras?.let {
            val title = it.getString("title")
            val time = it.getString("time")
            val imgUrl = it.getString("imgUrl")
            val content = it.getString("content")

            tvDetailTitle.text = title
            tvDetailTime.text = time
            tvDetailContent.text = content

            val dp4 = DisplayUtils.dip2px(this@DetailsActivity, 8f)
            GlideV4.getInstance().displayImage(this@DetailsActivity, imgUrl,
                    ivDetails, R.color.color_f7, RoundedCorners(dp4))
        }
    }
}