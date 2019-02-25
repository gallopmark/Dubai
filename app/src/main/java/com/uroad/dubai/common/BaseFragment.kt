package com.uroad.dubai.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.uroad.dubai.R
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.library.compat.AppDialog
import com.uroad.library.compat.AppToast
import com.uroad.library.compat.LoadingDialog
import com.uroad.library.utils.DeviceUtils
import com.uroad.library.utils.NetworkUtils
import com.uroad.library.widget.CurrencyLoadView

abstract class BaseFragment : Fragment() {
    private var rootView: View? = null
    open lateinit var context: Activity
    open lateinit var fgBaseParent: FrameLayout
    open lateinit var fgBaseLoadView: CurrencyLoadView
    open var contentView: View? = null
    private var mShortToast: Toast? = null
    private var mLongToast: Toast? = null
    private var mLoadingDialog: LoadingDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_base, container, false).apply {
                fgBaseParent = findViewById(R.id.fgBaseParent)
                fgBaseLoadView = findViewById(R.id.fgBaseLoadView)
            }
        }
        rootView?.parent?.let { (it as ViewGroup).removeView(rootView) }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view, savedInstanceState)
    }

    open fun setUp(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    open fun initData() {

    }

    open fun setContentView(@LayoutRes layoutResID: Int) {
        contentView = LayoutInflater.from(context).inflate(layoutResID, fgBaseParent, false)
        fgBaseParent.addView(contentView)
    }

    open fun onPageLoading() {
        contentView?.visibility = View.GONE
        fgBaseLoadView.setState(CurrencyLoadView.State.STATE_LOADING)
    }

    open fun onPageResponse() {
        contentView?.visibility = View.VISIBLE
        fgBaseLoadView.setState(CurrencyLoadView.State.STATE_IDEA)
    }

    open fun onPageError() {
        contentView?.visibility = View.GONE
        if (!NetworkUtils.isConnected(context))
            fgBaseLoadView.setState(CurrencyLoadView.State.STATE_NO_NETWORK)
        else
            fgBaseLoadView.setState(CurrencyLoadView.State.STATE_ERROR)
        fgBaseLoadView.setOnRetryListener(object : CurrencyLoadView.OnRetryListener {
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
        contentView?.visibility = View.GONE
        fgBaseLoadView.setState(CurrencyLoadView.State.STATE_EMPTY)
        if (!TextUtils.isEmpty(emptyTips)) fgBaseLoadView.setEmptyText(emptyTips)
        if (emptyIcon != -1) fgBaseLoadView.setEmptyIco(emptyIcon)
    }

    open fun onPageRetry(view: View) {
        initData()
    }

    // 封装跳转
    fun openActivity(c: Class<*>) {
        openActivity(c, null)
    }

    // 跳转 传递数据 bundel
    fun openActivity(c: Class<*>, bundle: Bundle?) {
        openActivity(c, bundle, null)
    }

    fun openActivity(c: Class<*>, bundle: Bundle?, uri: Uri?) {
        if (activity == null) return
        val intent = Intent(context, c)
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
        if (activity == null) return
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
        if (activity == null) return
        val intent = Intent(context, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    open fun openActivityForResult(intent: Intent, requestCode: Int) {
        openActivityForResult(intent, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(intent, bundle, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        if (activity == null) return
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    open fun showTipsDialog(message: CharSequence?) {
        showTipsDialog(message, "", null)
    }

    open fun showTipsDialog(message: CharSequence?, textPositive: CharSequence?, listener: AppDialog.OnClickListener?) {
        val dialog = AppDialog(context)
        dialog.setTitle(context.getString(R.string.dialog_default_title))
        dialog.setMessage(message)
        dialog.hideDivider()
        val text = if (TextUtils.isEmpty(textPositive)) context.getString(R.string.dialog_button_confirm) else textPositive
        dialog.setPositiveButton(text, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                if (listener == null) dialog.dismiss()
                else listener.onClick(v, dialog)
            }
        })
        dialog.show()
    }

    open fun showDialog(@StringRes messageRes: Int, listener: SimpleDialogInterface?) {
        showDialog(context.getString(R.string.dialog_default_title), context.getString(messageRes), context.getString(R.string.dialog_button_cancel), context.getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(@StringRes messageRes: Int, @StringRes confirmCsRes: Int, listener: SimpleDialogInterface?) {
        showDialog(context.getString(R.string.dialog_default_title), context.getString(messageRes), context.getString(R.string.dialog_button_cancel), context.getString(confirmCsRes), listener)
    }

    open fun showDialog(messageRes: CharSequence?, confirmCsRes: CharSequence?, listener: SimpleDialogInterface?) {
        showDialog(context.getString(R.string.dialog_default_title), messageRes, context.getString(R.string.dialog_button_cancel), confirmCsRes, listener)
    }

    open fun showDialog(@StringRes titleRes: Int, @StringRes messageRes: Int, listener: DialogViewClickListener?) {
        showDialog(context.getString(titleRes), context.getString(messageRes), context.getString(R.string.dialog_button_cancel), context.getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, listener: DialogViewClickListener?) {
        showDialog(title, message, context.getString(R.string.dialog_button_cancel), context.getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(@StringRes titleRes: Int, @StringRes messageRes: Int, @StringRes cancelCsRes: Int, @StringRes confirmCsRes: Int, listener: DialogViewClickListener?) {
        showDialog(context.getString(titleRes), context.getString(messageRes), context.getString(cancelCsRes), context.getString(confirmCsRes), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, cancelCs: CharSequence?, confirmCs: CharSequence?, listener: DialogViewClickListener?) {
        val dialog = AppDialog(context)
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
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, LinearLayout(context), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mShortToast == null) {
            mShortToast = AppToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_SHORT }
        }
        mShortToast?.let {
            it.view = v
            it.show()
        }
    }

    open fun showLongToast(text: CharSequence?) {
        if (TextUtils.isEmpty(text)) return
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, LinearLayout(context), false)
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        if (mLongToast == null) {
            mLongToast = AppToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
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
        mLoadingDialog = LoadingDialog(context).setMsg(msg).apply { show() }
    }

    open fun endLoading() {
        mLoadingDialog?.let {
            it.dismiss()
            mLoadingDialog = null
        }
    }

    override fun onDestroyView() {
        mShortToast?.cancel()
        mLongToast?.cancel()
        mLoadingDialog?.dismiss()
        super.onDestroy()
        super.onDestroyView()
    }

    fun getUserId() = UserPreferenceHelper.getUserId(context)

    fun getUserUUID() = UserPreferenceHelper.getUserUUID(context)

    fun getAndroidID() = DeviceUtils.getAndroidID(context)

    fun isLogin() = UserPreferenceHelper.isLogin(context)
}