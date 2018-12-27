package com.uroad.dubai.common

import android.os.Bundle
import android.view.View
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePresenterFragment<P : BasePresenter<*>> : BaseFragment(), BaseView {
    open var presenter: P? = null

    override fun setUp(view: View, savedInstanceState: Bundle?) {
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

    override fun onDestroyView() {
        presenter?.detachView()
        super.onDestroyView()
    }
}