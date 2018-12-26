package com.uroad.dubai.common

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView


abstract class BasePresenterActivity<P : BasePresenter<*>> : BaseActivity(), BaseView {
    open var presenter: P? = null

    protected abstract fun createPresenter(): P

    override fun initData() {
        presenter = createPresenter()
        onPresenterCreate()
    }

    open fun onPresenterCreate() {}

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