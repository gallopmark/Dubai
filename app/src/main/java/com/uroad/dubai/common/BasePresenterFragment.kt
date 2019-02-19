package com.uroad.dubai.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView

abstract class BasePresenterFragment<P : BasePresenter<*>> : BaseDubaiFragment(), BaseView {
    open lateinit var presenter: P

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = createPresenter()
        return super.onCreateView(inflater, container, savedInstanceState)
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