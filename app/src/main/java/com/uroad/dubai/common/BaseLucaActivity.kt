package com.uroad.dubai.common

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.widget.CurrencyLoadView
import com.uroad.library.compat.AppDialog
import com.uroad.library.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_base.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * 多语言设置（阿拉伯文会导致布局页面翻转）
 * 最后加入不需要进行翻转 我们需要在布局的XML文件中写android:layoutDirection="ltr"
 */
abstract class BaseLucaActivity : AppCompatActivity() {

    private var baseContentView: View? = null
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
        withOption(null, option, null)
    }

    open fun withOption(actionIcon: Drawable?, option: CharSequence?, onClickListener: View.OnClickListener?) {
        if (!TextUtils.isEmpty(option) || actionIcon != null) {
            val view = layoutInflater.inflate(R.layout.menu_action_base, baseToolbar, true)
            val baseMenuTv = view.findViewById<TextView>(R.id.baseMenuTv)
            baseMenuTv.text = option
            baseMenuTv.setCompoundDrawablesWithIntrinsicBounds(actionIcon, null, null, null)
            baseMenuTv.compoundDrawablePadding = 5
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
        baseLoadView.setState(CurrencyLoadView.STATE_LOADING)
    }

    open fun onPageResponse() {
        baseContentView?.visibility = View.VISIBLE
        baseLoadView.setState(CurrencyLoadView.STATE_IDEA)
    }

    open fun onPageError() {
        baseContentView?.visibility = View.GONE
        if (!NetworkUtils.isConnected(this))
            baseLoadView.setState(CurrencyLoadView.STATE_NO_NETWORK)
        else
            baseLoadView.setState(CurrencyLoadView.STATE_ERROR)
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
        baseLoadView.setState(CurrencyLoadView.STATE_EMPTY)
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

    open fun addDisposable(disposable: Disposable?) {
        disposable?.let { disposables.add(it) }
    }

    open fun removeDisposable(disposable: Disposable?) {
        disposable?.let { disposables.remove(disposable) }
    }

    open fun showTipsDialog(message: CharSequence?) {
        showTipsDialog(message, "", null)
    }

    open fun showTipsDialog(message: CharSequence?, textPositive: CharSequence?, listener: AppDialog.OnClickListener?) {
        val dialog = AppDialog(this)
        dialog.setTitle(getString(R.string.tips))
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

    open fun showDialog(title: CharSequence?, message: CharSequence?, listener: DialogViewClickListener?) {
        showDialog(title, message, getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, cancelCs: CharSequence?, confirmCs: CharSequence?, listener: DialogViewClickListener?) {
        val dialog = AppDialog(this)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton(cancelCs, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
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

    override fun onDestroy() {
        if (disposables.size() > 0) disposables.dispose()
        super.onDestroy()
    }

    interface DialogViewClickListener {
        fun onCancel(v: View, dialog: AppDialog)
        fun onConfirm(v: View, dialog: AppDialog)
    }
}