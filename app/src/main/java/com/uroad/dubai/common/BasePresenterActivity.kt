package com.uroad.dubai.common

import android.os.Bundle
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.BaseView
import org.jetbrains.annotations.NotNull


abstract class BasePresenterActivity<P : BasePresenter<*>> : BaseDubaiActivity(), BaseView {

    open lateinit var presenter: P
    @NotNull
    protected abstract fun createPresenter(): P

    override fun setUp(savedInstanceState: Bundle?) {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = createPresenter()
        onPresenterCreate()
        super.onCreate(savedInstanceState)
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
        presenter.detachView()
    }
}