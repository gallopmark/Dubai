package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.mapbox.api.directions.v5.MapboxDirections
import com.uroad.dubai.R
import com.uroad.dubai.activity.RouteNavigationActivity
import com.uroad.dubai.adaptervp.MainSubscribeAdapter
import com.uroad.dubai.api.presenter.SubscribePresenter
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.library.widget.banner.BaseBannerAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mainfavorites.*

/**
 * @author MFB
 * @create 2019/1/22
 * @describe main page favorites
 */
class MainFavoritesFragment : BasePresenterFragment<SubscribePresenter>(), SubscribeView {

    private lateinit var data: MutableList<SubscribeMDL>
    private lateinit var adapter: MainSubscribeAdapter
    private lateinit var handler: Handler
    private var isLoadFavorites = false
    private var callback: OnRequestCallback? = null

    override fun createPresenter(): SubscribePresenter = SubscribePresenter(context, this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainfavorites)
        initViewParams()
        initial()
        handler = Handler()
    }

    private fun initViewParams() {
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun initial() {
        data = ArrayList()
        adapter = MainSubscribeAdapter(context, data).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<SubscribeMDL> {
                override fun onItemClick(t: SubscribeMDL, position: Int) {
                    val point = t.getDestinationPoint()
                    point?.let {
                        openActivity(RouteNavigationActivity::class.java, Bundle().apply {
                            putString("point", it.toJson())
                            putString("endPointName", t.endpoint)
                        })
                    }
                }
            })
        }
        banner.setAdapter(adapter)
    }

    override fun initData() {
        isLoadFavorites = true
        presenter.getSubscribeData(getAndroidID())
    }

    override fun onGetSubscribeData(data: MutableList<SubscribeMDL>) {
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
//        getRoutes()
        callback?.callback(this.data.isEmpty())
    }

    private fun getRoutes() {
        isLoadFavorites = false
        for (i in 0 until data.size) {
            val startPoint = data[i].getOriginPoint()
            val endPoint = data[i].getDestinationPoint()
            if (startPoint != null && endPoint != null) {
                requestRoutes(i, presenter.buildDirections(startPoint, endPoint, data[i].getProfile()))
            }
        }
    }

    private fun requestRoutes(position: Int, directions: MapboxDirections) {
        val disposable = Observable.fromCallable { directions.executeCall() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    val directionsRoute = response.body()?.routes()?.get(0)
                    directionsRoute?.let { route ->
                        data[position].distance = route.distance()
                        data[position].travelTime = route.duration()
                        val congestion = route.legs()?.get(0)?.annotation()?.congestion()
                        data[position].congestion = congestion
                    }
                    adapter.notifyDataSetChanged()
                }, {
                    directions.cancelCall()
                    handler.postDelayed({ requestRoutes(position, directions) }, DubaiApplication.DEFAULT_DELAY_MILLIS)
                })
        presenter.addDisposable(disposable)
    }

    override fun onShowError(msg: String?) {
        if (isLoadFavorites) {
            handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        }
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        if (isLoadFavorites) {
            handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        }
    }

    interface OnRequestCallback {
        fun callback(isEmpty: Boolean)
    }

    fun setOnRequestCallback(callback: OnRequestCallback) {
        this.callback = callback
    }
}