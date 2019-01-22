package com.uroad.dubai.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.common.BaseFragment
import com.uroad.library.utils.DisplayUtils
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.photopicker.utils.ImagePicker
import com.uroad.glidev4.GlideApp
import com.uroad.glidev4.GlideV4
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 我的
 */
class MineFragment : BaseFragment() {

    private var isLogin : Boolean = false

    @SuppressLint("NewApi")
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mine)
        initTopLayout()

        isLogin = UserPreferenceHelper.isLogin(context)
        tvMessage.setOnClickListener{
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        tvFavorites.setOnClickListener{
            if (check()) return@setOnClickListener
            openActivity(FavoriteListActivity::class.java)
        }
        tvCalendar.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(CalendarListActivity::class.java)
        }
        tvSetting.setOnClickListener {
            openActivity(SettingActivity::class.java)
        }

        tvContactRTA.setOnClickListener {
            showTipsDialog(getString(R.string.developing))
        }

        tvShare.setOnClickListener {
            showTipsDialog(getString(R.string.developing))
        }

        tvFeedback.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(FeedbackActivity::class.java)
        }

        ivUserHead.setOnClickListener {
            if (check()) return@setOnClickListener
            /*ImagePicker.with(this@MineFragment)
                    .isCompress(true)
                    .requestCode(123)
                    .start()*/
        }
        if (isLogin){
            ivUserHead.setImageResource(R.mipmap.ic_user_default)
        }else{
            ivUserHead.setImageResource(R.mipmap.icon_user_head_gray)
        }
    }

    private fun initTopLayout() {
        val paddingTop = llUserInfo.paddingTop + DisplayUtils.getStatusHeight(context)
        llUserInfo.setPadding(llUserInfo.paddingStart, paddingTop, llUserInfo.paddingEnd, llUserInfo.paddingBottom)
    }

    private fun check() : Boolean{
        isLogin = UserPreferenceHelper.isLogin(context)
        if (!isLogin){
            openActivity(LoginActivity::class.java)
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        isLogin = UserPreferenceHelper.isLogin(context)
        if (isLogin){
            ivUserHead.setImageResource(R.mipmap.ic_user_default)
            upDataUserName("Emma",isLogin)
        }else{
            ivUserHead.setImageResource(R.mipmap.icon_user_head_gray)
            upDataUserName("Me",isLogin)
        }
    }

    @SuppressLint("NewApi")
    private fun upDataUserName(userName : String, isLogin : Boolean){
        if (isLogin){
            val span = SpannableStringBuilder(userName)
            val ss = StyleSpan(Typeface.BOLD)
            span.setSpan(ss,0,userName.length ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUserName.text = span
            tvUserName.setOnClickListener(null)
        }else{
            val loginStr = getString(R.string.login)
            val singupStr = getString(R.string.singup)
            val dp =  context.resources.getInteger(R.integer.integer_14)
            val str = "$userName\n$loginStr/$singupStr"
            val span = SpannableString(str)
            val ab = AbsoluteSizeSpan(dp,true)
            val ss = StyleSpan(Typeface.BOLD)
            span.setSpan(ab,userName.length, str.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(ss,0,userName.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUserName.text = span
            tvUserName.highlightColor = Color.TRANSPARENT
            tvUserName.movementMethod = LinkMovementMethod.getInstance()
            tvUserName.setOnClickListener(viewClick)
        }

    }

    private val viewClick : View.OnClickListener = View.OnClickListener {
        openActivity(LoginActivity::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == 123 && resultCode == Activity.RESULT_OK ){
            val images = data?.getStringArrayListExtra(ImagePicker.EXTRA_PATHS)
            images?.let {
                GlideV4.getInstance().displayCircleImage(context,images[0],ivUserHead,R.mipmap.ic_user_default)
            }
        }
    }
}