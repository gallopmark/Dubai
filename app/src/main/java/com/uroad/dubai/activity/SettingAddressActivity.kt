package com.uroad.dubai.activity

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.PoiSearchAdapter
import com.uroad.dubai.api.presenter.PoiSearchPresenter
import com.uroad.dubai.api.presenter.UserAddressPresenter
import com.uroad.dubai.api.view.PoiSearchView
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.common.BaseMapBoxLocationActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.model.UserAddressMDL
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_settingaddress.*
import java.util.*

/**
 * setting home or work address
 */
class SettingAddressActivity : BaseMapBoxLocationActivity(), PoiSearchView, UserAddressView {

    private val handler = Handler()
    private var poiKey: String? = ""
    private lateinit var poiPresenter: PoiSearchPresenter
    private var myLocation: Location? = null
    private lateinit var data: MutableList<CarmenFeature>
    private lateinit var poiAdapter: PoiSearchAdapter
    private var type = AddressType.HOME.CODE
    private lateinit var presenter: UserAddressPresenter
    private var isSetUp = false
    private var mAddressClient: MapboxGeocoding? = null
    private var myLocationAddress: String? = null
    private var needSetUp = false

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentViewWithoutTitle(R.layout.activity_settingaddress, true)
        poiPresenter = PoiSearchPresenter(this, this)
        presenter = UserAddressPresenter(this)
        initBundle()
        initViewData()
        openLocation()
    }

    private fun initBundle() {
        intent.extras?.let { type = it.getString("type", AddressType.HOME.CODE) }
    }

    private fun initViewData() {
        cvSearch.layoutParams = (cvSearch.layoutParams as RelativeLayout.LayoutParams).apply { this.topMargin = topMargin + DisplayUtils.getStatusHeight(this@SettingAddressActivity) }
        ivBack.setOnClickListener { onBackPressed() }
        initEtSearch()
        data = ArrayList()
        poiAdapter = PoiSearchAdapter(this, data).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position !in 0 until data.size) return
                    val center = data[position].center()
                    if (TextUtils.equals(data[position].id(), "myLocation")) {
                        center?.let { convertAddress(it.latitude(), it.longitude()) }
                    } else {
                        center?.let { setUpUserAddress(data[position].placeName(), it.longitude(), it.latitude()) }
                    }
                }
            })
        }
        rvPoi.adapter = poiAdapter
    }

    private fun initEtSearch() {
        etSearch.inputType = EditorInfo.TYPE_CLASS_TEXT
        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.clearFocus()
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            /*when click search button save content*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = etSearch.text.toString()
                if (!TextUtils.isEmpty(content)) {
                    handleWhenInput(content)
                }
            }
            return@setOnEditorActionListener true
        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
                val content = cs.toString()
                if (TextUtils.isEmpty(content)) {
                    whenContentEmpty()
                } else {
                    whenContentFill()
                    handleWhenInput(content)
                }
            }

            override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }


    /*when etSearch content empty*/
    private fun whenContentEmpty() {
        if (tvSearchTips.visibility != View.VISIBLE) tvSearchTips.visibility = View.VISIBLE
        removeRunnable()
    }

    /*when etSearch fill content*/
    private fun whenContentFill() {
        if (tvSearchTips.visibility != View.GONE) tvSearchTips.visibility = View.GONE
    }

    private fun removeRunnable() {
        handler.removeCallbacks(poiSearchRun)
        rvPoi.visibility = View.GONE
    }

    private fun handleWhenInput(content: String) {
        removeRunnable()
        poiKey = content
        handler.postDelayed(poiSearchRun, 500)
    }

    private val poiSearchRun = Runnable {
        poiPresenter.cancelCall()
        poiKey?.let {
            isSetUp = false
            poiPresenter.doPoiSearch(it)
        }
    }

    override fun afterLocation(location: Location) {
        myLocation = location
        addFirst()
        needSetUp = false
        doAddressSearch(location.longitude, location.latitude)
        poiAdapter.notifyDataSetChanged()
    }

    private fun addFirst() {
        data.clear()
        myLocation?.let {
            val feature = CarmenFeature.builder()
                    .id("myLocation")
                    .placeName(getString(R.string.route_myLocation))
                    .rawCenter(doubleArrayOf(it.longitude, it.latitude))
                    .build()
            this.data.add(feature)
        }
    }

    private fun doAddressSearch(longitude: Double, latitude: Double) {
        mAddressClient = poiPresenter.doPoiSearch(Point.fromLngLat(longitude, latitude), object : PoiSearchPresenter.PointSearchCallback {
            override fun onPointResult(features: MutableList<CarmenFeature>) {
                if (features.size > 0) {
                    myLocationAddress = features[0].placeName()
                    if (needSetUp) {
                        setUpUserAddress(myLocationAddress, longitude, latitude)
                    }
                }
            }
        })
    }

    override fun onPoiResult(features: MutableList<CarmenFeature>) {
        if (features.size > 0) {
            addFirst()
            this.data.addAll(features)
            poiAdapter.notifyDataSetChanged()
            rvPoi.visibility = View.VISIBLE
        }
    }

    private fun convertAddress(latitude: Double, longitude: Double) {
        if (TextUtils.isEmpty(myLocationAddress)) {
            needSetUp = true
            doAddressSearch(longitude, latitude)
        } else {
            setUpUserAddress(myLocationAddress, longitude, latitude)
        }
    }

    private fun setUpUserAddress(address: String?, longitude: Double, latitude: Double) {
        isSetUp = true
        presenter.setUpUserAddress(getUserUUID(), address, "$longitude,$latitude", type)
    }

    override fun onShowLoading() {
        if (isSetUp) showLoading()
    }

    override fun onHideLoading() {
        if (isSetUp) endLoading()
    }

    override fun onShowError(msg: String?) {
        if (isSetUp) showShortToast(msg)
    }

    override fun onSuccess(data: String?, funType: Int) {
        showLongToast(getString(R.string.setup_success))
        setResult(RESULT_OK)
        Handler().postDelayed({ if (!isFinishing) finish() }, 2000L)
    }

    override fun onGetUserAddress(data: MutableList<UserAddressMDL>) {
    }

    override fun onFailure(errMsg: String?, errCode: Int?) {
        if (isSetUp) showShortToast(errMsg)
    }

    override fun onDestroy() {
        mAddressClient?.cancelCall()
        poiPresenter.detachView()
        presenter.detachView()
        super.onDestroy()
    }
}