package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import kotlinx.android.synthetic.main.activity_share.*
import android.content.Intent
import android.net.Uri


class ShareActivity : BaseActivity(){

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle("Share")
        setBaseContentView(R.layout.activity_share)

        initView()
    }

    private fun initView(){
        tvShare.setOnClickListener {
            share("我是内容")
        }

        tvRate.setOnClickListener { showTipsDialog(getString(R.string.developing)) }

        tvTarms.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
    }

    private fun share(content: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "$content\nhttp://www.baidu.com")
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, "Invite friends"))
        //系统默认标题
    }
}