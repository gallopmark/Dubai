package com.uroad.dubai.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.uroad.dubai.R
import com.uroad.dubai.adaptervp.GuideAdapter
import com.uroad.dubai.common.BaseLucaActivity
import com.uroad.dubai.local.AppSource
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.ref.WeakReference

class SplashActivity : BaseLucaActivity() {

    private var handler: MHandler? = null
    private var isGoMain = false
    private var second = 5
    private val updateCode = 0x0001

    private class MHandler(activity: SplashActivity) : Handler() {
        private val weakReference: WeakReference<SplashActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = weakReference.get() ?: return
            if (activity.second <= 0) {
                removeMessages(activity.updateCode)
                if (!activity.isGoMain) activity.openMainPage()
            } else {
                activity.tvJump.visibility = View.VISIBLE
                val delayText = "${activity.getString(R.string.skip)}\u2000" + activity.second + "s"
                activity.tvJump.text = delayText
                activity.second--
                sendEmptyMessageDelayed(activity.updateCode, 1000)
            }
        }
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_splash)
        if (AppSource.isGuide(this)) {
            onSplash()
        } else {
            onGuide()
            AppSource.saveGuide(this)
        }
    }

    private fun onSplash() {
        handler = MHandler(this).apply { sendEmptyMessage(updateCode) }
        flSplash.visibility = View.VISIBLE
        ivSplash.setImageResource(R.mipmap.ic_splash_bg)
        tvJump.setOnClickListener {
            handler?.removeMessages(updateCode)
            if (!isGoMain) openMainPage()
        }
    }

    private fun onGuide() {
        flGuide.visibility = View.VISIBLE
        val pictures = arrayListOf(R.mipmap.ic_guide_graph1, R.mipmap.ic_guide_graph2, R.mipmap.ic_guide_graph3)
        val indicators = ArrayList<ImageView>()
        llIndicator.removeAllViews()
        for (i in 0 until pictures.size) {
            val imageView = ImageView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { rightMargin = DisplayUtils.dip2px(this@SplashActivity, 10f) } }
            if (i == 0) imageView.setImageResource(R.mipmap.ic_indicator_selected)
            else imageView.setImageResource(R.mipmap.ic_indicator_default)
            indicators.add(imageView)
            llIndicator.addView(imageView)
        }
        val adapter = GuideAdapter(this, pictures)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == pictures.size - 1) tvHome.visibility = View.VISIBLE
                else tvHome.visibility = View.INVISIBLE
                for (i in 0 until indicators.size) {
                    //选中的页面改变小圆点为选中状态，反之为未选中
                    if (position == i) indicators[i].setImageResource(R.mipmap.ic_indicator_selected)
                    else indicators[i].setImageResource(R.mipmap.ic_indicator_default)
                }
            }
        })
        tvHome.setOnClickListener { openMainPage() }
    }

    private fun openMainPage() {
        openActivity(MainActivity::class.java)
        isGoMain = true
        finish()
    }

    override fun onDestroy() {
        handler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}