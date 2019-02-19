package com.uroad.dubai.activity

import android.os.Build
import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.FeedbackPresenter
import com.uroad.dubai.api.view.FeedbackView
import com.uroad.library.common.BaseActivity
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_feed_back.*

class FeedbackActivity : BaseActivity() , FeedbackView{

    private lateinit var presenter: FeedbackPresenter

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.mine_feedback))
        setBaseContentView(R.layout.activity_feed_back)

        initView()
    }

    private fun initView(){
        presenter = FeedbackPresenter(this@FeedbackActivity)
        rbScore.max = 5
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            rbScore.min = 1
        }

        val uuId = UserPreferenceHelper.getUserUUID(this@FeedbackActivity)
        val nickname = UserPreferenceHelper.getUserNickName(this@FeedbackActivity)
        val phone = UserPreferenceHelper.getPhone(this@FeedbackActivity)

        btnSubmit.setOnClickListener {
            val trim = edContent.text.toString().trim()
            val size = rbScore.rating
            if (trim.isEmpty() || size == 0f) return@setOnClickListener
            presenter.post(WebApi.SAVEFEEDBACK,
                    WebApi.saveFeedBack("$uuId", "$nickname", "$size", trim, "$phone"))
        }
    }

    override fun onPostSuccess(msg: String) {
        showShortToast(msg)
        if (msg.contains("Successfully")) finish()
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {

    }

    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {

    }

}