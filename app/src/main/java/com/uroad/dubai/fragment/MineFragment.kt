package com.uroad.dubai.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.api.presenter.FunctionStatisticsPresenter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.enumeration.StatisticsType
import com.uroad.library.utils.DisplayUtils
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.photopicker.utils.ImagePicker
import com.uroad.glidev4.GlideV4
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 我的
 */
class MineFragment : BaseFragment() {

    private lateinit var presenter: FunctionStatisticsPresenter
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mine)
        presenter = FunctionStatisticsPresenter(context)
        initTopLayout()
        initLayout()
        initView()
    }

    private fun initTopLayout() {
        val paddingTop = llUserInfo.paddingTop + DisplayUtils.getStatusHeight(context)
        llUserInfo.setPadding(llUserInfo.paddingStart, paddingTop, llUserInfo.paddingEnd, llUserInfo.paddingBottom)
    }

    private fun initLayout() {
        val statusHeight = DisplayUtils.getStatusHeight(context)
        ivUserHeadTop.layoutParams = (ivUserHeadTop.layoutParams as ConstraintLayout.LayoutParams).apply { leftMargin = statusHeight }
        toolbar.layoutParams = (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply {
            topMargin = statusHeight
            leftMargin = statusHeight
        }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            if (offset <= total / 2) {
                ivUserHeadTop.visibility = View.GONE
            } else {
                ivUserHeadTop.visibility = View.VISIBLE
            }
        })
    }

    private fun initView() {
        tvMessage.setOnClickListener { onViewClick(1) }
        tvFavorites.setOnClickListener { onViewClick(2) }
        tvCalendar.setOnClickListener { onViewClick(3) }
        tvSetting.setOnClickListener { openSettingsPage() }
        tvContactRTA.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvShare.setOnClickListener { openSharePage() }
        tvFeedback.setOnClickListener { onViewClick(4) }
        tvAppTips.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvAbout.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvTerm.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivUserHead.setOnClickListener { onViewClick(5) }
        ivUserHeadTop.setOnClickListener { onViewClick(5) }
        if (isLogin()) {
            ivUserHead.setImageResource(R.mipmap.ic_user_default)
            ivUserHeadTop.setImageResource(R.mipmap.ic_user_default)
        } else {
            ivUserHead.setImageResource(R.mipmap.icon_user_head_gray)
            ivUserHeadTop.setImageResource(R.mipmap.icon_user_head_gray)
        }
    }

    private fun onViewClick(type: Int) {
        if (!isLogin()) {
            openActivity(LoginActivity::class.java)
        } else {
            openPageByType(type)
        }
    }

    private fun openPageByType(type: Int) {
        when (type) {
            1 -> openMessagePage()
            2 -> openFavouritePage()
            3 -> openCalendarPage()
            4 -> openFeedbackPage()
            5 -> openActivity(PersonalInformationActivity::class.java)
        }
    }

    private fun openMessagePage() {
        presenter.onFuncStatistics(StatisticsType.ME_MESSAGE.CODE)
        openActivity(MessagesListActivity::class.java)
    }

    private fun openFavouritePage() {
        presenter.onFuncStatistics(StatisticsType.ME_FAVOURITES.CODE)
        openActivity(FavoriteListActivity::class.java)
    }

    private fun openCalendarPage() {
        openActivity(CalendarListActivity::class.java)
    }

    private fun openSharePage() {
        presenter.onFuncStatistics(StatisticsType.ME_SHARE.CODE)
        openActivity(ShareActivity::class.java)
    }

    private fun openFeedbackPage() {
        presenter.onFuncStatistics(StatisticsType.ME_FEEDBACK.CODE)
        openActivity(FeedbackActivity::class.java)
    }

    private fun openSettingsPage() {
        presenter.onFuncStatistics(StatisticsType.ME_SETTINGS.CODE)
        openActivity(SettingActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (isLogin()) {
            GlideV4.displayCircleImage(context,
                    UserPreferenceHelper.getAvatar(context),
                    ivUserHeadTop, R.mipmap.ic_user_default)
            /*loadCirclePic(context,
                    UserPreferenceHelper.getAvatar(context)?:"",ivUserHeadTop)*/

            loadCirclePic(context,
                    UserPreferenceHelper.getAvatar(context) ?: "", ivUserHead)

            upDataUserName(UserPreferenceHelper.getUserNickName(context) ?: "Emma", isLogin())
        } else {
            ivUserHead.setImageResource(R.mipmap.icon_user_head_gray)
            upDataUserName("Me", isLogin())
        }
    }

    @SuppressLint("NewApi")
    private fun upDataUserName(userName: String, isLogin: Boolean) {
        if (isLogin) {
            val span = SpannableStringBuilder(userName)
            val ss = StyleSpan(Typeface.BOLD)
            span.setSpan(ss, 0, userName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUserName.text = span
            tvUserName.setOnClickListener(null)
        } else {
            val loginStr = getString(R.string.login)
            val singupStr = getString(R.string.singup)
            val dp = context.resources.getInteger(R.integer.integer_14)
            val str = "$userName\n$loginStr/$singupStr"
            val span = SpannableString(str)
            val ab = AbsoluteSizeSpan(dp, true)
            val ss = StyleSpan(Typeface.BOLD)
            span.setSpan(ab, userName.length, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(ss, 0, userName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUserName.text = span
            tvUserName.highlightColor = Color.TRANSPARENT
            tvUserName.movementMethod = LinkMovementMethod.getInstance()
            tvUserName.setOnClickListener(viewClick)
        }

    }

    private val viewClick: View.OnClickListener = View.OnClickListener {
        openActivity(LoginActivity::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val images = data?.getStringArrayListExtra(ImagePicker.EXTRA_PATHS)
            images?.let {
                GlideV4.displayCircleImage(context, images[0], ivUserHead, R.mipmap.ic_user_default)
            }
        }
    }

    private fun loadCirclePic(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        imageView.setImageDrawable(circularBitmapDrawable)
                    }
                })
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}