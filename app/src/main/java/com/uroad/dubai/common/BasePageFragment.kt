package com.uroad.dubai.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.uroad.dubai.R
import com.uroad.dubai.widget.CurrencyLoadView
import com.uroad.library.compat.AppToast
import com.uroad.library.compat.LoadingDialog
import com.uroad.library.utils.NetworkUtils

/* viewpager结合fragment使用 使用懒加载处理，避免重复加载数据*/
abstract class BasePageFragment : Fragment() {
    lateinit var context: Activity
    private var rootView: View? = null
    open lateinit var fgBaseParent: FrameLayout
    open lateinit var fgBaseLoadView: CurrencyLoadView
    open var contentView: View? = null
    private var isFragmentVisible: Boolean = false
    private var isFirstVisible: Boolean = false
    private var isViewInit: Boolean = false
    private var mShortToast: Toast? = null
    private var mLongToast: Toast? = null
    private var mLoadingDialog: LoadingDialog? = null
    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (rootView == null || !isViewInit) return
        if (isFirstVisible && isVisibleToUser) {
            initData()
            isFirstVisible = false
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true)
            isFragmentVisible = true
            return
        }
        if (isFragmentVisible) {
            isFragmentVisible = false
            onFragmentVisibleChange(false)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstVisible = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = rootView ?: inflater.inflate(R.layout.fragment_base, container, false).apply {
            fgBaseParent = findViewById(R.id.fgBaseParent)
            fgBaseParent = findViewById(R.id.fgBaseParent)
            fgBaseLoadView = findViewById(R.id.fgBaseLoadView)
            setUp(this)
            setListener()
            isViewInit = true
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (userVisibleHint) {
            if (isFirstVisible) {
                initData()
                isFirstVisible = false
            }
            onFragmentVisibleChange(true)
            isFragmentVisible = true
        }
    }

    open fun setContentView(@LayoutRes layoutResID: Int) {
        contentView?.let { fgBaseParent.removeView(it) }
        contentView = LayoutInflater.from(context).inflate(layoutResID, fgBaseParent, false)
        fgBaseParent.addView(contentView)
    }

    open fun setUp(view: View) {}
    open fun setListener() {}
    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    open fun initData() {}

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     * @param isVisible true  不可见 -> 可见
     * false 可见  -> 不可见
     */
    open fun onFragmentVisibleChange(isVisible: Boolean) {}

    open fun onPageLoading() {
        contentView?.visibility = View.GONE
        fgBaseLoadView.setState(CurrencyLoadView.STATE_LOADING)
    }

    open fun onPageResponse() {
        contentView?.visibility = View.VISIBLE
        fgBaseLoadView.setState(CurrencyLoadView.STATE_IDEA)
    }

    open fun onPageError() {
        contentView?.visibility = View.GONE
        if (!NetworkUtils.isConnected(this@BasePageFragment.context))
            fgBaseLoadView.setState(CurrencyLoadView.STATE_NO_NETWORK)
        else
            fgBaseLoadView.setState(CurrencyLoadView.STATE_ERROR)
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
        fgBaseLoadView.setState(CurrencyLoadView.STATE_EMPTY)
        if (!TextUtils.isEmpty(emptyTips)) fgBaseLoadView.setEmptyText(emptyTips)
        if (emptyIcon != -1) fgBaseLoadView.setEmptyIco(emptyIcon)
    }

    open fun onPageRetry(view: View) {
        initData()
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

    override fun onDestroyView() {
        rootView?.parent?.let { (it as ViewGroup).removeView(rootView) }
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mShortToast?.cancel()
        mLongToast?.cancel()
        mLoadingDialog?.dismiss()
    }
}