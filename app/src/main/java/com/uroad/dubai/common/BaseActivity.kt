package com.uroad.dubai.common

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.uroad.dubai.R
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.utils.DubaiUtils
import com.uroad.library.compat.AppDialog
import com.uroad.library.compat.AppToast
import com.uroad.library.compat.LoadingDialog
import com.uroad.library.utils.DeviceUtils
import com.uroad.library.utils.NetworkUtils
import com.uroad.library.widget.CurrencyLoadView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_base.*

/**
 * 多语言设置（阿拉伯文会导致布局页面翻转）
 * 最后加入不需要进行翻转 我们需要在布局的XML文件中写android:layoutDirection="ltr"
 */
/*需要toast提示时的页面继承改activity*/
abstract class BaseActivity : AppCompatActivity() {
    private var baseContentView: View? = null
    private var mShortToast: Toast? = null
    private var mLongToast: Toast? = null
    private var mLoadingDialog: LoadingDialog? = null
    private val disposables = CompositeDisposable()

//    override fun attachBaseContext(context: Context) {
//        val language = LanguageHelper.getLanguage(context)
//        super.attachBaseContext(LanguageUtil.attachBaseContext(context, language))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindow()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制竖屏
        setContentView(R.layout.activity_base)
        setUpToolbar()
        setUp(savedInstanceState)
        initData()
    }

    open fun requestWindow() {}
    /**
     * 通过设置全屏，设置状态栏透明
     */
    open fun requestWindowFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT)
        } else {
            val attributes = window.attributes
            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            attributes.flags = flagTranslucentStatus
            window.attributes = attributes
        }
    }

    open fun setUpToolbar() {
        baseToolbar.title = ""
        setSupportActionBar(baseToolbar)
        actionBar?.setDisplayShowTitleEnabled(false)
        baseToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    open fun setUp(savedInstanceState: Bundle?) {

    }

    open fun initData() {}

    open fun withTitle(title: CharSequence?) {
        baseTitleTv.text = title
    }

    open fun withOption(option: CharSequence?) {
        withOption(null, option)
    }

    open fun withOption(actionIcon: Drawable?, option: CharSequence?) {
        withOption(actionIcon, option, null)
    }

    open fun withOption(option: CharSequence?, onClickListener: View.OnClickListener?) {
        withOption(null, option, onClickListener)
    }

    open fun withOption(actionIcon: Drawable?, option: CharSequence?, onClickListener: View.OnClickListener?) {
        if (!TextUtils.isEmpty(option) || actionIcon != null) {
            getOptionView()?.let { baseToolbar.removeView(it) }
            val view = layoutInflater.inflate(R.layout.menu_action_base, baseToolbar, true)
            val baseMenuTv = view.findViewById<TextView>(R.id.baseMenuTv)
            baseMenuTv.text = option
            baseMenuTv.setCompoundDrawablesWithIntrinsicBounds(actionIcon, null, null, null)
            baseMenuTv.compoundDrawablePadding = 5
            baseMenuTv.setOnClickListener(onClickListener)
        }
    }

    open fun withOption(option: CharSequence?, onClickListener: View.OnClickListener?, color: Int) {
        if (!TextUtils.isEmpty(option)) {
            val view = layoutInflater.inflate(R.layout.menu_action_base, baseToolbar, true)
            val baseMenuTv = view.findViewById<TextView>(R.id.baseMenuTv)
            baseMenuTv.text = option
            baseMenuTv.setTextColor(color)
            baseMenuTv.setOnClickListener(onClickListener)
        }
    }

    open fun withOption(actionIcon: Drawable?, option: CharSequence?,
                        drawablePadding: Int,
                        onClickListener: View.OnClickListener?) {
        if (!TextUtils.isEmpty(option) || actionIcon != null) {
            val view = layoutInflater.inflate(R.layout.menu_action_base, baseToolbar, true)
            val baseMenuTv = view.findViewById<TextView>(R.id.baseMenuTv)
            baseMenuTv.text = option
            baseMenuTv.setCompoundDrawablesWithIntrinsicBounds(actionIcon, null, null, null)
            baseMenuTv.compoundDrawablePadding = drawablePadding
            baseMenuTv.setTextColor(Color.BLACK)
            baseMenuTv.setOnClickListener(onClickListener)
        }
    }

    open fun getOptionView(): TextView? = baseToolbar.findViewById(R.id.baseMenuTv)

    open fun setBaseContentView(@LayoutRes layoutResID: Int) {
        baseContentView = layoutInflater.inflate(layoutResID, baseParent, false)
        baseParent.addView(baseContentView)
    }

    open fun setBaseContentView(contentView: View?) {
        contentView?.let {
            baseParent.addView(contentView)
            baseContentView = it
        }
    }

    open fun setBaseContentView(@LayoutRes layoutResID: Int, hideLine: Boolean) {
        if (hideLine) baseParent.removeView(baseLine)
        baseContentView = layoutInflater.inflate(layoutResID, baseParent, false)
        baseParent.addView(baseContentView)
    }

    open fun setBaseContentView(contentView: View?, hideLine: Boolean) {
        if (hideLine) baseParent.removeView(baseLine)
        contentView?.let {
            baseParent.addView(contentView)
            baseContentView = it
        }
    }

    open fun setBaseContentViewWithoutTitle(@LayoutRes layoutId: Int) {
        setBaseContentViewWithoutTitle(layoutId, false)
    }

    open fun setBaseContentViewWithoutTitle(contentView: View?) {
        setBaseContentViewWithoutTitle(contentView, false)
    }

    open fun setBaseContentViewWithoutTitle(@LayoutRes layoutId: Int, hideLine: Boolean) {
        baseParent.removeView(baseToolbar)
        if (hideLine) baseParent.removeView(baseLine)
        setBaseContentView(layoutId)
    }

    open fun setBaseContentViewWithoutTitle(contentView: View?, hideLine: Boolean) {
        baseParent.removeView(baseToolbar)
        if (hideLine) baseParent.removeView(baseLine)
        setBaseContentView(contentView)
    }

    open fun removeBaseLine() {
        baseParent.removeView(baseToolbar)
    }

    open fun onPageLoading() {
        baseContentView?.visibility = View.GONE
        baseLoadView.setState(CurrencyLoadView.State.STATE_LOADING)
    }

    open fun onPageResponse() {
        baseContentView?.visibility = View.VISIBLE
        baseLoadView.setState(CurrencyLoadView.State.STATE_IDEA)
    }

    open fun onPageError() {
        baseContentView?.visibility = View.GONE
        if (!NetworkUtils.isConnected(this))
            baseLoadView.setState(CurrencyLoadView.State.STATE_NO_NETWORK)
        else
            baseLoadView.setState(CurrencyLoadView.State.STATE_ERROR)
        baseLoadView.setOnRetryListener(object : CurrencyLoadView.OnRetryListener {
            override fun onRetry(view: View) {
                onPageRetry(view)
            }
        })
    }

    open fun onPageNoData() {
        onPageNoData(-1, null)
    }

    open fun onPageNoData(emptyTips: CharSequence?) {
        onPageNoData(-1, emptyTips)
    }

    open fun onPageNoData(emptyIcon: Int, emptyTips: CharSequence?) {
        baseContentView?.visibility = View.GONE
        baseLoadView.setState(CurrencyLoadView.State.STATE_EMPTY)
        if (!TextUtils.isEmpty(emptyTips)) baseLoadView.setEmptyText(emptyTips)
        if (emptyIcon != -1) baseLoadView.setEmptyIco(emptyIcon)
    }

    open fun onPageRetry(view: View) {
        initData()
    }

    // 封装跳转
    fun openActivity(c: Class<*>) {
        openActivity(c, null)
    }

    // 跳转 传递数据 bundel
    open fun openActivity(c: Class<*>, bundle: Bundle?) {
        openActivity(c, bundle, null)
    }

    open fun openActivity(c: Class<*>, bundle: Bundle?, uri: Uri?) {
        val intent = Intent(this, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivity(intent)
    }

    fun openActivity(intent: Intent) {
        openActivity(intent, null)
    }

    open fun openActivity(intent: Intent, bundle: Bundle?) {
        openActivity(intent, bundle, null)
    }

    open fun openActivity(intent: Intent, bundle: Bundle?, uri: Uri?) {
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = uri }
        startActivity(intent)
    }

    fun openActivityForResult(c: Class<*>, requestCode: Int) {
        openActivityForResult(c, null, requestCode)
    }

    open fun openActivityForResult(c: Class<*>, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(c, bundle, null, requestCode)
    }

    open fun openActivityForResult(c: Class<*>, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        val intent = Intent(this, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    fun openActivityForResult(intent: Intent, requestCode: Int) {
        openActivityForResult(intent, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(intent, bundle, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    open fun showTipsDialog(message: CharSequence?) {
        showTipsDialog(message, "", null)
    }

    open fun showTipsDialog(message: CharSequence?, textPositive: CharSequence?, listener: AppDialog.OnClickListener?) {
        val dialog = AppDialog(this)
        dialog.setTitle(getString(R.string.dialog_default_title))
        dialog.setMessage(message)
        dialog.hideDivider()
        val text = if (TextUtils.isEmpty(textPositive)) getString(R.string.dialog_button_confirm) else textPositive
        dialog.setPositiveButton(text, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                if (listener == null) dialog.dismiss()
                else listener.onClick(v, dialog)
            }
        })
        dialog.show()
    }

    open fun showDialog(@StringRes messageRes: Int, listener: SimpleDialogInterface?) {
        showDialog(getString(R.string.dialog_default_title), getString(messageRes), getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(@StringRes messageRes: Int, @StringRes confirmCsRes: Int, listener: SimpleDialogInterface?) {
        showDialog(getString(R.string.dialog_default_title), getString(messageRes), getString(R.string.dialog_button_cancel), getString(confirmCsRes), listener)
    }

    open fun showDialog(messageRes: CharSequence?, confirmCsRes: CharSequence?, listener: SimpleDialogInterface?) {
        showDialog(getString(R.string.dialog_default_title), messageRes, getString(R.string.dialog_button_cancel), confirmCsRes, listener)
    }

    open fun showDialog(@StringRes titleRes: Int, @StringRes messageRes: Int, listener: DialogViewClickListener?) {
        showDialog(getString(titleRes), getString(messageRes), getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, listener: DialogViewClickListener?) {
        showDialog(title, message, getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(@StringRes titleRes: Int, @StringRes messageRes: Int, @StringRes cancelCsRes: Int, @StringRes confirmCsRes: Int, listener: DialogViewClickListener?) {
        showDialog(getString(titleRes), getString(messageRes), getString(cancelCsRes), getString(confirmCsRes), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, cancelCs: CharSequence?, confirmCs: CharSequence?, listener: DialogViewClickListener?) {
        val dialog = AppDialog(this)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton(cancelCs, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                if (listener != null && listener is SimpleDialogInterface) {
                    dialog.dismiss()
                }
                listener?.onCancel(v, dialog)
            }
        })
        dialog.setPositiveButton(confirmCs, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                listener?.onConfirm(v, dialog)
            }
        })
        dialog.show()
    }

    interface DialogViewClickListener {
        fun onCancel(v: View, dialog: AppDialog)
        fun onConfirm(v: View, dialog: AppDialog)
    }

    abstract class SimpleDialogInterface : DialogViewClickListener {
        override fun onCancel(v: View, dialog: AppDialog) {

        }
    }

    open fun showShortToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(this).inflate(R.layout.layout_compat_toast, LinearLayout(this), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mShortToast == null) {
            mShortToast = AppToast(this, R.style.CompatToast).apply { duration = Toast.LENGTH_SHORT }
        }
        mShortToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLongToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(this).inflate(R.layout.layout_compat_toast, LinearLayout(this), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mLongToast == null) {
            mLongToast = AppToast(this, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
        }
        mLongToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLoading() {
        showLoading("")
    }

    open fun showLoading(msg: CharSequence?) {
        mLoadingDialog?.let {
            if (it.isShowing) it.dismiss()
            mLoadingDialog = null
        }
        mLoadingDialog = LoadingDialog(this).setMsg(msg).apply { show() }
    }

    open fun endLoading() {
        mLoadingDialog?.let {
            it.dismiss()
            mLoadingDialog = null
        }
    }

    open fun addDisposable(disposable: Disposable?) {
        disposable?.let { disposables.add(it) }
    }

    open fun removeDisposable(disposable: Disposable?) {
        disposable?.let { disposables.remove(disposable) }
    }

    fun getUserId() = UserPreferenceHelper.getUserId(this)

    fun getUserUUID() = UserPreferenceHelper.getUserUUID(this)

    fun getUserName() = UserPreferenceHelper.getUserName(this)

    fun isLogin() = UserPreferenceHelper.isLogin(this)
    fun getTestUserId() = "201901227175316"

    fun getAndroidID() = DeviceUtils.getAndroidID(this)

    fun openSettings() {
        DubaiUtils.openSettings(this)
    }

    override fun onDestroy() {
        mShortToast?.cancel()
        mLongToast?.cancel()
        mLoadingDialog?.dismiss()
        if (disposables.size() > 0) disposables.dispose()
        super.onDestroy()
    }

    open fun drawable(id: Int) = ContextCompat.getDrawable(this@BaseActivity, id)

}