package com.uroad.dubai.common

import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.utils.DubaiUtils
import com.uroad.library.common.BaseActivity
import com.uroad.library.utils.DeviceUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * 多语言设置（阿拉伯文会导致布局页面翻转）
 * 最后加入不需要进行翻转 我们需要在布局的XML文件中写android:layoutDirection="ltr"
 */
abstract class BaseDubaiActivity : BaseActivity() {

    private val disposables = CompositeDisposable()

    open fun addDisposable(disposable: Disposable?) {
        disposable?.let { disposables.add(it) }
    }

    open fun removeDisposable(disposable: Disposable?) {
        disposable?.let { disposables.remove(disposable) }
    }

    fun getUserId() = UserPreferenceHelper.getUserId(this)

    fun getUserUUID() = UserPreferenceHelper.getUserUUID(this)

    fun isLogin() = UserPreferenceHelper.isLogin(this)
    fun getTestUserId() = "201901227175316"

    fun getAndroidID() = DeviceUtils.getAndroidID(this)

    override fun onDestroy() {
        if (disposables.size() > 0) disposables.dispose()
        super.onDestroy()
    }

    fun openSettings() {
        DubaiUtils.openSettings(this)
    }
}