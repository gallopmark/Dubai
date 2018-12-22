package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.adapter.MainBannerAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.library.banner.GalleryRecyclerView
import kotlinx.android.synthetic.main.fragment_mainbanner.*

class MainBannerFragment : BaseFragment() {
    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mainbanner)
        fgBaseParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        initBanner()
    }

    private fun initBanner() {
        galleryRv.isNestedScrollingEnabled = false
        val pictures = ArrayList<Int>().apply {
            add(R.mipmap.ic_mask)
            add(R.mipmap.ic_mask)
            add(R.mipmap.ic_mask)
        }
        progressBar.max = pictures.size
        galleryRv.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        galleryRv.adapter = MainBannerAdapter(context, pictures)
        galleryRv.initFlingSpeed(9000).initPageParams(6, 0)
                .initPosition(0)
                .setAnimFactor(0.1f).autoPlay(true)
                .setOnPageChangedListener(object : GalleryRecyclerView.OnPageChangedListener {
                    override fun onPageChange(position: Int) {
                        progressBar.progress = (position % pictures.size) + 1
                    }
                })
                .intervalTime(5000).setUp()
    }

    override fun onDestroyView() {
        galleryRv.release()
        super.onDestroyView()
    }
}