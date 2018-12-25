package com.uroad.dubai.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseLucaActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.ref.WeakReference

class SplashActivity : BaseLucaActivity() {

    private lateinit var handler: MHandler
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

    override fun requestWindow() {
        requestWindowFullScreen()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_splash)
        handler = MHandler(this).apply { sendEmptyMessage(updateCode) }
        initView()
    }

    private fun initView() {
        ivSplash.setImageResource(R.mipmap.ic_splash_bg)
        tvJump.setOnClickListener {
            handler.removeMessages(updateCode)
            if (!isGoMain) openMainPage()
        }
    }

    private fun openMainPage() {
        openActivity(MainActivity::class.java)
        isGoMain = true
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}