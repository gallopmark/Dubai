package com.uroad.dubai.photopicker.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate
import android.view.View
import com.uroad.dubai.R
import com.uroad.library.common.BaseActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.photopicker.adapter.ImagePageAdapter
import com.uroad.dubai.photopicker.model.ImageItem
import com.uroad.dubai.photopicker.utils.AnimationUtil
import com.uroad.dubai.photopicker.adapter.ImageRvAdapter
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_photopicker_preview.*

class ImagePreViewActivity : BaseActivity() {
    private val photos = ArrayList<ImageItem>()
    private val checkItems = ArrayMap<Int, ImageItem>()
    private lateinit var pageAdapter: ImagePageAdapter
    private lateinit var imageAdapter: ImageRvAdapter
    private var isOpen = true
    private var onRightClick = false

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_photopicker_preview, true)
        initPhotos()
        initDataView()
        addViewListener()
    }

    private fun initPhotos() {
        intent.extras?.let {
            val data = it.getParcelableArrayList<ImageItem>("photos")
            if (data.size > 0) {
                this.photos.addAll(data)
                for (i in 0 until this.photos.size) {
                    checkItems[i] = this.photos[i]
                }
            }
        }
        if (this.photos.size > 0) {
            withTitle("1/${this.photos.size}")
            setFinishButton()
        } else {
            withTitle("0/0")
        }
    }

    private fun initDataView() {
        pageAdapter = ImagePageAdapter(this, this.photos).apply {
            setOnItemClickListener(object : ImagePageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, path: ImageItem) {
                    isOpen = if (isOpen) {
                        closeView()
                        false
                    } else {
                        openView()
                        true
                    }
                }
            })
        }
        viewPager.adapter = pageAdapter
        imageAdapter = ImageRvAdapter(this, photos).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    viewPager.setCurrentItem(position, false)
                    imageAdapter.setSelectIndex(position)
                }
            })
        }
        recyclerView.adapter = imageAdapter
    }

    private fun addViewListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                withTitle("${position + 1}/${photos.size}")
                imageAdapter.setSelectIndex(position)
                recyclerView.smoothScrollToPosition(position)
                imageAdapter.getUnCheckItems()[position]?.let { checkBox.isChecked = it }
            }
        })
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) checkItems.remove(viewPager.currentItem)
            else checkItems[viewPager.currentItem] = photos[viewPager.currentItem]
            imageAdapter.setUnCheckItem(viewPager.currentItem, isChecked)
            setFinishButton()
        }
    }

    private fun openView() {
        AnimationUtil.topMoveToViewLocation(baseToolbar, 200)
        AnimationUtil.bottomMoveToViewLocation(llBottom, 200)
        flContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    private fun closeView() {
        AnimationUtil.moveToViewBottom(llBottom, 200)
        AnimationUtil.moveToViewTop(baseToolbar, 200)
        flContainer.systemUiVisibility = View.INVISIBLE
    }

    private fun setFinishButton() {
        val text = "${resources.getString(R.string.photopicker_finish)}(${checkItems.size}/${photos.size})"
        withOption(text, View.OnClickListener {
            onRightClick = true
            finish()
        })
    }

    override fun finish() {
        val images = ArrayList<ImageItem>().apply { addAll(checkItems.values) }
        val intent = Intent().apply { putParcelableArrayListExtra("images", images) }
        if (onRightClick && images.size > 0) {
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED, intent)
        }
        super.finish()
    }
}