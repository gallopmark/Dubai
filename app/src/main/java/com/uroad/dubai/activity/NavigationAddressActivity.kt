package com.uroad.dubai.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.NavigationAddressAdapter
import com.uroad.dubai.api.presenter.UserAddressPresenter
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.SettingAddressMDL
import com.uroad.dubai.model.UserAddressMDL
import kotlinx.android.synthetic.main.activity_navigationaddress.*

class NavigationAddressActivity : BasePresenterActivity<UserAddressPresenter>(), UserAddressView {

    private lateinit var data: MutableList<MultiItem>
    private lateinit var adapter: NavigationAddressAdapter
    private var deleteIndex = -1
    private lateinit var handler: Handler
    private var isInitialization = false

    override fun createPresenter(): UserAddressPresenter = UserAddressPresenter(this)
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_navigationaddress)
        withTitle(getString(R.string.navigation_address))
        initDataView()
        handler = Handler()
    }

    private fun initDataView() {
        data = ArrayList()
        adapter = NavigationAddressAdapter(this, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    whenClickItem(position)
                }
            })
            setOnItemChildClickListener(object : BaseRecyclerAdapter.OnItemChildClickListener {
                override fun onItemChildClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position !in 0 until data.size) return
                    if (view.id == R.id.ivDelete && data[position].getItemType() == 2) {
                        deleteIndex = position
                        deleteItem(data[position] as UserAddressMDL)
                    }
                }
            })
        }
        recyclerView.adapter = adapter
        setDefault()
    }

    private fun setDefault() {
        data.add(getHomeMDL())
        data.add(getWorkMDL())
        adapter.notifyDataSetChanged()
    }

    private fun getHomeMDL() = SettingAddressMDL().apply {
        icon = R.mipmap.ic_route_home
        name = getString(R.string.route_home)
        tips = getString(R.string.set_home_address)
    }

    private fun getWorkMDL() = SettingAddressMDL().apply {
        icon = R.mipmap.ic_route_work
        name = getString(R.string.route_work)
        tips = getString(R.string.set_work_address)
    }

    private fun whenClickItem(position: Int) {
        if (position !in 0 until data.size) return
        val itemType = data[position].getItemType()
        if (itemType == 1) {
            val mdl = data[position] as SettingAddressMDL
            if (TextUtils.equals(mdl.name, getString(R.string.route_home))) {
                openActivityForResult(SettingAddressActivity::class.java, Bundle().apply { putString("type", AddressType.HOME.CODE) }, 1)
            } else {
                openActivityForResult(SettingAddressActivity::class.java, Bundle().apply { putString("type", AddressType.WORK.CODE) }, 1)
            }
        } else {
            val mdl = data[position] as UserAddressMDL
            var point: Point? = null
            var address: String? = null
            if (TextUtils.equals(mdl.addresstype, AddressType.HOME.CODE)) {
                mdl.getLatLng()?.let { point = Point.fromLngLat(it.longitude, it.latitude) }
                address = mdl.address
            } else if (TextUtils.equals(mdl.addresstype, AddressType.WORK.CODE)) {
                mdl.getLatLng()?.let { point = Point.fromLngLat(it.longitude, it.latitude) }
                address = mdl.address
            }
            point?.let { onNavigationRoute(it, address) }
        }
    }

    private fun onNavigationRoute(point: Point, endPointName: String?) {
        openActivity(RouteNavigationActivity::class.java, Bundle().apply {
            putString("endPoint", point.toJson())
            putString("endPointName", endPointName)
        })
    }

    override fun initData() {
        isInitialization = true
        presenter.getUserAddress(getTestUserId())
    }

    private fun deleteItem(mdl: UserAddressMDL) {
        isInitialization = false
        presenter.deleteUserAddress(mdl.addressid)
    }

    override fun onSuccess(data: String?, funType: Int) {
        if (deleteIndex == 0) {
            this.data.removeAt(0)
            this.data.add(0, getHomeMDL())
        } else if (deleteIndex == 1) {
            this.data.removeAt(1)
            this.data.add(1, getWorkMDL())
        }
        adapter.notifyDataSetChanged()
    }

    override fun onGetUserAddress(data: MutableList<UserAddressMDL>) {
        var homeMDL: UserAddressMDL? = null
        var workMDL: UserAddressMDL? = null
        for (item in data) {
            if (TextUtils.equals(item.addresstype, AddressType.HOME.CODE)) {
                homeMDL = item
            } else if (TextUtils.equals(item.addresstype, AddressType.WORK.CODE)) {
                workMDL = item
            }
        }
        homeMDL?.let {
            this.data.removeAt(0)
            this.data.add(0, it)
        }
        workMDL?.let {
            this.data.removeAt(1)
            this.data.add(1, it)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        if (isInitialization) handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onShowError(msg: String?) {
        if (isInitialization) handler.postDelayed({ initData() }, DubaiApplication.DEFAULT_DELAY_MILLIS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            initData()
        }
    }
}