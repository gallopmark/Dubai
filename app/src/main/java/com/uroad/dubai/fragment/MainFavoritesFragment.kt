package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.util.ArrayMap
import android.view.View
import android.widget.FrameLayout
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.uroad.dubai.R
import com.uroad.dubai.activity.RouteNavigationActivity
import com.uroad.dubai.adaptervp.MainSubscribeAdapter
import com.uroad.dubai.api.presenter.SubscribePresenter
import com.uroad.dubai.api.view.SubscribeView
import com.uroad.dubai.common.BasePresenterFragment
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.SubscribeMDL
import com.uroad.dubai.rx2bus.MessageEvent
import com.uroad.dubai.rx2bus.Rx2Bus
import com.uroad.library.widget.banner.BaseBannerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
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
    private lateinit var arrayMap: ArrayMap<Int, MapboxDirections>
    private var callback: OnRequestCallback? = null

    override fun createPresenter(): SubscribePresenter = SubscribePresenter(context, this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainfavorites)
        initViewParams()
        initial()
        handler = Handler()
        toObservable()
    }

    private fun initViewParams() {
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun initial() {
        arrayMap = ArrayMap()
        data = ArrayList()
        adapter = MainSubscribeAdapter(context, data).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<SubscribeMDL> {
                override fun onItemClick(t: SubscribeMDL, position: Int) {
                    openActivity(RouteNavigationActivity::class.java, Bundle().apply {
                        putBoolean("fromHome", true)
                        putString("routeId", t.routeid)
                        putString("profile", t.getProfile())
                        putString("startPoint", t.getOriginPoint()?.toJson())
                        putString("startPointName", t.startpoint)
                        putString("endPoint", t.getDestinationPoint()?.toJson())
                        putString("endPointName", t.endpoint)
                    })
                }
            })
        }
        banner.setAdapter(adapter)
    }

    private fun toObservable() {
        presenter.addDisposable(Rx2Bus.toObservable(MessageEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { initData() })
    }

    override fun initData() {
        presenter.getSubscribeData(getAndroidID())
    }

    override fun onGetSubscribeData(data: MutableList<SubscribeMDL>) {
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
        getRoutes()
        callback?.callback(this.data.isEmpty())
    }

    private fun getRoutes() {
        presenter.requestRoutes(data, object : SubscribePresenter.SubscribeRouteCallback {
            override fun getRoutes(map: ArrayMap<Int, DirectionsRoute>) {
                for ((k, v) in map) {
                    data[k].distance = v.distance()
                    data[k].travelTime = v.duration()
                    val congestion = v.legs()?.get(0)?.annotation()?.congestion()
                    data[k].congestion = congestion
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onShowError(msg: String?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    interface OnRequestCallback {
        fun callback(isEmpty: Boolean)
        fun onFailure()
    }

    fun setOnRequestCallback(callback: OnRequestCallback) {
        this.callback = callback
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}