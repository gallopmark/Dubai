package com.uroad.dubai.activity

import android.os.Bundle
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.EventsPresenter
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.model.EventsMDL
import com.uroad.glidev4.GlideV4
import com.uroad.library.utils.BitmapUtils
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_eventdetail.*

/**
 * @author MFB
 * @create 2019/1/17
 * @describe event detail activity
 */
class EventsDetailActivity : BasePresenterActivity<EventsPresenter>(), EventsPresenter.EventDetailView {

    private var eventId: String? = null
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_eventdetail)
        withTitle(getString(R.string.details))
        initBundle()
        setTopImage()
    }

    override fun createPresenter(): EventsPresenter = EventsPresenter()
    private fun initBundle() {
        eventId = intent.extras?.getString("eventId")
    }

    private fun setTopImage() {
        val imageWidth = DisplayUtils.getWindowWidth(this)
        val imageHeight = DisplayUtils.getWindowHeight(this) / 3
        ivBg.layoutParams = (ivBg.layoutParams as LinearLayout.LayoutParams).apply { height = imageHeight }
        ivBg.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(resources, R.mipmap.ic_eventdetail_bg, imageWidth, imageHeight))
    }

    override fun initData() {
        presenter.getEventDetails(eventId, getUserUUID(), this)
    }

    override fun onShowLoading() {
        onPageLoading()
    }

    override fun onGetEventDetail(eventMDL: EventsMDL) {
        updateUI(eventMDL)
    }

    override fun onShowError(msg: String?) {
        onPageError()
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