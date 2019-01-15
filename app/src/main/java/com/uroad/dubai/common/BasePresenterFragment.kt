package com.uroad.dubai.common

import android.os.Bundle
import android.view.View
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePresenterFragment<P : BasePresenter<*>> : BaseFragment(), BaseView {
    open lateinit var presenter: P

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = createPresenter()
    }

    abstract fun createPresenter(): P


    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowError(msg: String?) {

    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}