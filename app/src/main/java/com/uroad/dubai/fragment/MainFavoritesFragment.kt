package com.uroad.dubai.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.FrameLayout
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private var callback: OnRequestCallback? = null

    companion object {
        private const val CODE_RETRY = 100
        private const val CODE_ROUTE = 101
        private const val CODE_ROUTE_RETRY = 102
    }

    private class MHandler(fragment: MainFavoritesFragment) : Handler() {
        private val weakReference = WeakReference<MainFavoritesFragment>(fragment)

        override fun handleMessage(msg: Message) {
            val fragment = weakReference.get() ?: return
            when (msg.what) {
                CODE_RETRY -> fragment.initData()
                CODE_ROUTE -> {
                    val position = msg.arg1
                    val route = msg.obj as DirectionsRoute
                    fragment.data[position].distance = route.distance()
                    fragment.data[position].travelTime = route.duration()
                    val congestion = route.legs()?.get(0)?.annotation()?.congestion()
                    fragment.data[position].congestion = congestion
                    fragment.adapter.notifyDataSetChanged()
                }
                CODE_ROUTE_RETRY -> {
                    val position = msg.arg1
                    fragment.enqueueRoute(position)
                }
            }
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
        data = ArrayList()
        adapter = MainSubscribeAdapter(context, data).apply {
            setOnItemClickListener(object : BaseBannerAdapter.OnItemClickListener<SubscribeMDL> {
                override fun onItemClick(t: SubscribeMDL, position: Int) {
                    val point = t.getDestinationPoint()
                    point?.let {
                        openActivity(RouteNavigationActivity::class.java, Bundle().apply {
                            putBoolean("fromHome", true)
                            putString("routeId", t.routeid)
                            putString("profile", t.getProfile())
                            putString("point", it.toJson())
                            putString("endPointName", t.endpoint)
                        })
                    }
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
        for (i in 0 until data.size) {
            handler.postDelayed({ enqueueRoute(i) }, i * 100L)
        }
    }

    private fun enqueueRoute(position: Int) {
        if (position !in 0 until data.size) return
        val startPoint = data[position].getOriginPoint()
        val endPoint = data[position].getDestinationPoint()
        if (startPoint != null && endPoint != null) {
            requestRoutes(position, presenter.buildDirections(startPoint, endPoint, data[position].getProfile()))
        }
    }

    private fun requestRoutes(position: Int, directions: MapboxDirections) {
        directions.cloneCall().enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                activity ?: return
                val directionsRoute = response.body()?.routes()?.get(0)
                directionsRoute?.let { route ->
                    val msg = Message().apply {
                        what = CODE_ROUTE
                        arg1 = position
                        obj = route
                    }
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                activity ?: return
                val msg = Message().apply {
                    what = CODE_ROUTE_RETRY
                    arg1 = position
                }
                handler.sendMessage(msg)
            }
        })
    }

    override fun onShowError(msg: String?) {
        handler.sendEmptyMessageDelayed(CODE_RETRY, DubaiApplication.DEFAULT_DELAY_MILLIS)
        callback?.onFailure()
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        handler.sendEmptyMessageDelayed(CODE_RETRY, DubaiApplication.DEFAULT_DELAY_MILLIS)
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