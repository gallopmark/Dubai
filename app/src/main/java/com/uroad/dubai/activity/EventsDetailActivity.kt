package com.uroad.dubai.activity

import android.os.Bundle
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.library.utils.BitmapUtils
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_eventdetail.*

/**
 * @author MFB
 * @create 2019/1/17
 * @describe event detail activity
 */
class EventsDetailActivity : BaseActivity() {
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_eventdetail)
        withTitle(getString(R.string.details))
        setTopImage()
        initBundle()
    }

    private fun setTopImage() {
        val imageWidth = DisplayUtils.getWindowWidth(this)
        val imageHeight = DisplayUtils.getWindowHeight(this) / 3
        ivBg.layoutParams = (ivBg.layoutParams as LinearLayout.LayoutParams).apply { height = imageHeight }
        ivBg.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(resources, R.mipmap.ic_eventdetail_bg, imageWidth, imageHeight))
    }

    private fun initBundle() {
        val json = intent.extras?.getString("eventMDL")
        val mdl = GsonUtils.fromJsonToObject(json, EventsMDL::class.java)
        mdl?.let { updateUI(mdl) }
    }

    private fun updateUI(mdl: EventsMDL) {
        ivIcon.setImageResource(mdl.iconInt)
        tvTitle.text = mdl.roadtitle
        tvTime.text = mdl.updatetime
        tvContent.text = mdl.reportout
        tvStartTime.text = "14:23"
        tvHandleTime.text = "14:35"
        tvEndTime.text = "15:12"
        tvDistance.text = "5km"
    }
}