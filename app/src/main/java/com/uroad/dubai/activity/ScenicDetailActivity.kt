package com.uroad.dubai.activity

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.view.View
import android.widget.FrameLayout
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseLucaActivity
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.ScenicMDL
import com.uroad.glidev4.GlideV4
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_scenicdetail.*

/**
 * @author MFB
 * @create 2019/1/3
 * @describe
 */
class ScenicDetailActivity : BaseLucaActivity() {

    override fun requestWindow() {
        requestWindowFullScreen()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_scenicdetail, true)
        initView()
        initLayout()
    }

    private fun initView() {
        ivBack.setOnClickListener { onBackPressed() }
        ivBack2.setOnClickListener { onBackPressed() }
        llTopLayout.setOnClickListener {
            DubaiApplication.clickItemScenic?.let { scenic ->
                val point = Point.fromLngLat(scenic.getLatLng().longitude, scenic.getLatLng().latitude)
                openActivity(RouteNavigationActivity::class.java, Bundle().apply {
                    putString("point", point.toJson())
                    putString("endPointName", scenic.title)
                })
            }
        }
    }

    private fun initLayout() {
        val statusHeight = DisplayUtils.getStatusHeight(this)
        ivBack.layoutParams = (ivBack.layoutParams as FrameLayout.LayoutParams).apply { topMargin = statusHeight }
        toolbar.layoutParams = (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply { topMargin = statusHeight }
        val picHeight = DisplayUtils.getWindowHeight(this) / 3
        flTopPic.layoutParams = (flTopPic.layoutParams as CollapsingToolbarLayout.LayoutParams).apply { height = picHeight }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            if (offset <= total / 2) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {
        val mdl = DubaiApplication.clickItemScenic
        if (mdl != null) updateUI(mdl)
    }

    private fun updateUI(mdl: ScenicMDL) {
        GlideV4.getInstance().displayImage(this, mdl.headimg, ivTopPic)
        tvSubTitle.text = mdl.title
        tvAddress.text = mdl.address
        tvContent.text = mdl.content
        tvHours.text = mdl.hours
        tvTel.text = mdl.phone
        tvTitle.text = mdl.title
    }

    override fun onDestroy() {
        DubaiApplication.clickItemScenic = null
        super.onDestroy()
    }
}