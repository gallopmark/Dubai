package com.uroad.dubai.common

import android.view.View
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePagePresenterFragment<P : BasePresenter<*>> : BasePageFragment(), BaseView {
    open var presenter: P? = null

    override fun setUp(view: View) {
        presenter = createPresenter()
        onPresenterSetUp(view)
    }

    abstract fun createPresenter(): P?

    open fun onPresenterSetUp(view: View) {}

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