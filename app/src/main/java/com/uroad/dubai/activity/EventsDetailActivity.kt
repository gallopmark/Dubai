package com.uroad.dubai.activity

import android.os.Bundle
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.glidev4.GlideV4
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
        GlideV4.getInstance().displayImage(this, mdl.icon, ivIcon)
        tvTitle.text = mdl.roadname
        mDirectionTv.text = mdl.direction
        tvContent.text = mdl.eventinfo
        tvStartTime.text = mdl.starttime
        tvHandleTime.text = mdl.handldtime
        tvEndTime.text = mdl.endtime
        var distance = "0km"
        mdl.congestiondistance?.let { distance = "${it}km" }
        tvDistance.text = distance
    }
}