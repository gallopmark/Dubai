package com.uroad.dubai.common

import android.os.Bundle
import android.view.View
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePagePresenterFragment<P : BasePresenter<*>> : BasePageFragment(), BaseView {
    open var presenter: P? = null

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        initViewData(view, savedInstanceState)
        presenter = createPresenter()
    }

    abstract fun createPresenter(): P?

    abstract fun initViewData(view: View, savedInstanceState: Bundle?)

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