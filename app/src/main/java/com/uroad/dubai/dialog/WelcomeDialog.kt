package com.uroad.dubai.dialog

import android.app.Dialog
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.uroad.dubai.R

/**
 * @author MFB
 * @create 2019/1/17
 * @describe if first install show this dialog
 */
class WelcomeDialog(private val mContext: Context)
    : Dialog(mContext, R.style.AppDialog) {
    private val title = "Welcome to Dubai!"
    private val message = "Thank you for using RTA Smart. This app allows you to:\n" +
            "-  Plan your routes and view travel times for various means of transportation\n" +
            "-  View information on the attractions near you, and across the city\n" +
            "-  View available parking lots near you\n" +
            "-  Synchronise daily sightseeing itineraries with your calendar"

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        window?.let { window ->
            val contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_welcome, ConstraintLayout(mContext), false)
            val tvTitle = contentView.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = contentView.findViewById<TextView>(R.id.tvMessage)
            val tvOk = contentView.findViewById<TextView>(R.id.tvOk)
            tvTitle.text = title
            tvMessage.text = message
            tvMessage.movementMethod = ScrollingMovementMethod.getInstance()
            tvOk.setOnClickListener { dismiss() }
            window.setContentView(contentView)
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
        }
    }
}