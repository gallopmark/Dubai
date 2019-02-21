package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.util.ArrayMap
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.tencent.bugly.proguard.v
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mainfavorites.*
import java.lang.ref.WeakReference

/**
 * @author MFB
 * @create 2019/1/22
 * @describe main page favorites
 */
class MainFavoritesFragment : BasePresenterFragment<SubscribePresenter>(), SubscribeView {

    private lateinit var data: MutableList<SubscribeMDL>
    private lateinit var adapter: MainSubscribeAdapter
    private lateinit var handler: MHandler
    private lateinit var arrayMap: ArrayMap<Int, MapboxDirections>
    private var callback: OnRequestCallback? = null

    private class MHandler(fragment: MainFavoritesFragment) : Handler() {
        private val weakReference: WeakReference<MainFavoritesFragment> = WeakReference(fragment)
        override fun handleMessage(msg: Message) {
            val fragment = weakReference.get() ?: return
            val position = msg.arg1
            val directionsRoute = msg.obj as DirectionsRoute
            Log.e("obj","${directionsRoute.toJson()}")
            fragment.data[position].distance = directionsRoute.distance()
            fragment.data[position].travelTime = directionsRoute.duration()
            val congestion = directionsRoute.legs()?.get(0)?.annotation()?.congestion()
            fragment.data[position].congestion = congestion
            fragment.adapter.notifyDataSetChanged()
        }
    }

    override fun createPresenter(): SubscribePresenter = SubscribePresenter(context, this)

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainfavorites)
        initViewParams()
        initial()
        handler = MHandler(this)
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
        generateRoutes()
        callback?.callback(this.data.isEmpty())
    }

    private fun generateRoutes() {
        generateMap()
        getRoutes()
    }

    private fun generateMap() {
        for (i in 0 until data.size) {
            val startPoint = data[i].getOriginPoint()
            val endPoint = data[i].getDestinationPoint()
            startPoint ?: continue
            endPoint ?: continue
            arrayMap[i] = presenter.buildDirections(startPoint, endPoint, data[i].getProfile())
        }
    }

    private fun getRoutes() {
        presenter.addDisposable(Observable.fromCallable {
            for ((k, v) in arrayMap) {
                try {
                    sendMessage(k, requestRoute(v))
                } catch (e: Exception) {
                    continue
                }
            }
        }.subscribeOn(Schedulers.io()).subscribe())
    }

    private fun requestRoute(directions: MapboxDirections): DirectionsRoute? {
        val response = directions.executeCall()
        return response.body()?.routes()?.get(0)
    }

    private fun sendMessage(position: Int, directionsRoute: DirectionsRoute?) {
        handler.sendMessage(Message().apply {
            arg1 = position
            obj = directionsRoute
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