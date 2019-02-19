package com.uroad.dubai.common

import android.os.Bundle
import android.view.View
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePageRefreshPresenterFragment<P : BasePresenter<*>> : BasePageRefreshFragment(), BaseView {
    open var presenter: P? = null

    override fun initViewData(view: View, savedInstanceState: Bundle?) {
        presenter = createPresenter()
        onViewReady(view, savedInstanceState)
    }

    abstract fun onViewReady(view: View, savedInstanceState: Bundle?)
    abstract fun createPresenter(): P?

    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }
}