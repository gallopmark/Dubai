package com.uroad.dubai.photopicker.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.view.KeyEvent
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.photopicker.adapter.ImageFolderAdapter
import com.uroad.dubai.photopicker.adapter.ImageGridAdapter
import com.uroad.dubai.photopicker.data.ImagePickerPresenter
import com.uroad.dubai.photopicker.data.ImagePickerView
import com.uroad.dubai.photopicker.model.ImageFolder
import com.uroad.dubai.photopicker.model.ImageItem
import com.uroad.dubai.photopicker.utils.AnimationUtil
import kotlinx.android.synthetic.main.activity_photopicker.*
import kotlin.collections.ArrayList

/**
 * @author MFB
 * @create 2019/1/12
 */
class PhotoPickerActivity : BasePresenterActivity<ImagePickerPresenter>(), ImagePickerView {

    private var isLoaded = false
    private lateinit var imageFolders: MutableList<ImageFolder>
    private lateinit var folderAdapter: ImageFolderAdapter
    private lateinit var images: MutableList<ImageItem>
    private lateinit var gridAdapter: ImageGridAdapter

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_photopicker)
        withTitle(getString(R.string.photopicker_choose_albums))
        initBundle()
        initRvFolder()
        initFolder()
        initRvImage()
        initPermission()
    }

    override fun createPresenter(): ImagePickerPresenter = ImagePickerPresenter(this, this)

    private fun initPermission() {
        if (!presenter.hasExternalPermission()) {
            presenter.applyExternalPermission()
        } else {
            initAlbums()
        }
    }

    private fun initBundle() {
        presenter.initBundle(intent.extras)
        if (presenter.isMultiple) {
            tvPreview.visibility = View.VISIBLE
        } else {
            tvPreview.visibility = View.GONE
        }
    }

    private fun initRvFolder() {
        imageFolders = ArrayList()
        folderAdapter = ImageFolderAdapter(this, imageFolders).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    whenFolderItemClick(position)
                }
            })
        }
        rvFolder.adapter = folderAdapter
    }

    private fun whenFolderItemClick(position: Int) {
        if (position in 0 until imageFolders.size) {
            folderAdapter.setSelectedItem(position)
            images.clear()
            if (position == 0) {
                images.add(ImageItem().apply { showCamera = true })
            }
            images.addAll(imageFolders[position].mediaItems)
            gridAdapter.notifyDataSetChanged()
            tvText.text = imageFolders[position].name
            closeFolder()
        }
    }

    private fun initFolder() {
        flFolder.setOnClickListener { closeFolder() }
        rlFolder.setOnClickListener {
            if (flFolder.visibility == View.VISIBLE) {
                closeFolder()
            } else {
                openFolder()
            }
        }
    }

    private fun initRvImage() {
        (rvImage.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        (rvImage.itemAnimator as DefaultItemAnimator).changeDuration = 0
        images = ArrayList()
        gridAdapter = presenter.getImageGridAdapter(images)
        rvImage.adapter = gridAdapter
    }

    override fun onPermissionGranted() {
        initAlbums()
    }

    private fun initAlbums() {
        presenter.getImageFolders()
        isLoaded = true
    }

    override fun onShowLoading() {
        onPageLoading()
    }

    override fun onHideLoading() {
        onPageResponse()
    }

    override fun onGetImageFolders(folders: MutableList<ImageFolder>) {
        rlContainer.visibility = View.VISIBLE
        this.imageFolders.addAll(folders)
        folderAdapter.notifyDataSetChanged()
        images.add(ImageItem().apply { showCamera = true })
        if (folders.size > 0) {
            tvText.text = folders[0].name
            images.addAll(folders[0].mediaItems)
            gridAdapter.notifyDataSetChanged()
        }
    }

    override fun onFailure(errorMsg: String?) {
        showShortToast(errorMsg)
    }

    override fun onPreviewCallback(images: ArrayList<ImageItem>?, resultCode: Int) {
        images?.let { dealWith(it) }
        when (resultCode) {
            RESULT_OK -> images?.let { presenter.onResult(it) }
            RESULT_CANCELED -> images?.let { gridAdapter.setSelects(it) }
        }
    }

    override fun onImageSelected(images: ArrayList<ImageItem>) {
        dealWith(images)
    }

    override fun onResume() {
        super.onResume()
        if (presenter.hasExternalPermission() && !isLoaded) {
            initAlbums()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    private fun dealWith(data: ArrayList<ImageItem>) {
        var preview = resources.getString(R.string.photopicker_preview)
        var confirm = resources.getString(R.string.photopicker_confirm)
        if (data.size > 0) {
            getOptionView()?.isEnabled = true
            tvPreview.isEnabled = true
            confirm += "(${data.size}/${presenter.limit})"
            preview += "(${data.size})"
            tvPreview.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        } else {
            getOptionView()?.isEnabled = false
            tvPreview.isEnabled = false
            tvPreview.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
        getOptionView()?.text = confirm
        tvPreview.text = preview
        tvPreview.setOnClickListener { presenter.onPreview(data) }
        getOptionView()?.setOnClickListener { presenter.onResult(data) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && flFolder.visibility == View.VISIBLE) {
            closeFolder()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun openFolder() {
        AnimationUtil.bottomMoveToViewLocation(flFolder, 300)
    }

    /*** 收起文件夹列表*/
    private fun closeFolder() {
        AnimationUtil.moveToViewBottom(flFolder, 300)
    }
}