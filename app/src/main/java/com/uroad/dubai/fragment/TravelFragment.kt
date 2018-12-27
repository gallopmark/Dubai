package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import com.uroad.dubai.R
import com.uroad.dubai.activity.AttractionsListActivity
import com.uroad.dubai.activity.BusStopListActivity
import com.uroad.dubai.activity.ParkingListActivity
import com.uroad.dubai.activity.PoliceListActivity
import com.uroad.dubai.adapter.TravelRecommendAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.model.RecommendMDL
import com.uroad.library.banner.GalleryRecyclerView
import com.uroad.library.utils.BitmapUtils
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.travel_content_menu.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 旅游
 */
class TravelFragment : BaseFragment() {

    private val data = ArrayList<RecommendMDL>()
    private lateinit var adapter: TravelRecommendAdapter
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_travel)
        setTopImage()
        initRv()
        initView()
    }

    private fun initView(){
        var bundle = Bundle()

        tvHotels.setOnClickListener {
            bundle.putString("type","1001002")
            openActivity(AttractionsListActivity::class.java,bundle)
        }
        tvRestaurants.setOnClickListener {
            bundle.putString("type","1001003")
            openActivity(AttractionsListActivity::class.java,bundle)
        }
        tvAttractions.setOnClickListener {
            bundle.putString("type","1001004")
            openActivity(AttractionsListActivity::class.java,bundle)
        }
        tvTransport.setOnClickListener { openActivity(BusStopListActivity::class.java) }
        tvParking.setOnClickListener { openActivity(ParkingListActivity::class.java) }
        tvPolice.setOnClickListener { openActivity(PoliceListActivity::class.java) }
    }

    //重新计算图片高度 避免图片压缩
    private fun setTopImage() {
        val width = DisplayUtils.getWindowWidth(context)
        val height = (width * 0.67).toInt()
        ivTopPic.layoutParams = ivTopPic.layoutParams.apply {
            this.width = width
            this.height = height
        }
        ivTopPic.scaleType = ImageView.ScaleType.FIT_XY
        ivTopPic.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(resources, R.mipmap.ic_travel_topbg, width, height))
    }

    private fun initRv() {
        galleryRv.isNestedScrollingEnabled = false
        galleryRv.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        adapter = TravelRecommendAdapter(context, data)
        galleryRv.adapter = adapter
        galleryRv.initFlingSpeed(9000).initPageParams(6, 0)
                .initPosition(0)
                .setAnimFactor(0.1f).setUp()
    }

    override fun initData() {
        data.add(RecommendMDL().apply {
            title = "Palazzo Versace Dubai"
            content = getContent()
            address = "591 Geor Street, Paris, 75150, France"
            distance = "1,2 km"
        })
        data.add(RecommendMDL().apply {
            title = "Palazzo Versace Dubai"
            content = getContent()
            address = "591 Geor Street, Paris, 75150, France"
            distance = "2.1 km"
        })
        data.add(RecommendMDL().apply {
            title = "Palazzo Versace Dubai"
            content = getContent()
            address = "591 Geor Street, Paris, 75150, France"
            distance = "3.2 km"
        })
        data.add(RecommendMDL().apply {
            title = "Palazzo Versace Dubai"
            content = getContent()
            address = "591 Geor Street, Paris, 75150, France"
            distance = "5.6 km"
        })
    }

    private fun getContent(): CharSequence {
        val source = "Uae national day offer starting from AED 1199 with breakfast"
        val start = source.indexOf("A")
        val end = source.lastIndexOf("9") + 1
        val ts15 = context.resources.getDimensionPixelOffset(R.dimen.font_15)
        return SpannableString(source).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.theme_red)), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(AbsoluteSizeSpan(ts15, false), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}