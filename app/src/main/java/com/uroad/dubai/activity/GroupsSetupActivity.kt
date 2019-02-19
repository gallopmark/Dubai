package com.uroad.dubai.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.library.utils.BitmapUtils
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_groupssetup.*

/**
 * @author MFB
 * @create 2019/1/9
 * @describe organize a team
 */
class GroupsSetupActivity : BaseActivity() {
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_groupssetup)
        withTitle(getString(R.string.groups_title))
        initView()
    }

    private fun initView() {
        tvCreate.setOnClickListener { openActivity(GroupsEditActivity::class.java) }
        ivBackground.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(
                resources,
                R.mipmap.ic_groupsetup_bg,
                DisplayUtils.getWindowWidth(this),
                DisplayUtils.getWindowHeight(this)))
        initInput()
    }

    private fun initInput() {
        etInput.inputType = EditorInfo.TYPE_CLASS_TEXT
        etInput.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etInput.clearFocus()
        etInput.setOnEditorActionListener { _, actionId, _ ->
            /*when click search button save content*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = etInput.text.toString()
                if (!TextUtils.isEmpty(content)) {
                    showTipsDialog(getString(R.string.developing))
                } else {
                    showShortToast(getString(R.string.groups_input_tips))
                }
            }
            return@setOnEditorActionListener true
        }
    }
}