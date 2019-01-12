package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.zhgs.helper.UserPreferenceHelper
import kotlinx.android.synthetic.main.activity_create_password.*

class CreatePasswordActivity : BaseActivity() {

    private var password : String = ""
    private var isInvisible : Boolean = false

    @SuppressLint("NewApi")
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_create_password,true)

        btnConfirm.setOnClickListener {
            password = edCreate.text.toString().trim()
            if (TextUtils.isEmpty(password)){
                return@setOnClickListener
            }
            UserPreferenceHelper.saveUserPassword(this@CreatePasswordActivity, this.password)
            finish()
        }

        ivInvisible.setOnClickListener {
            isInvisible = !isInvisible
            if (isInvisible){
                edCreate.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(getDrawable(R.mipmap.icon_visual))
            } else{
                edCreate.transformationMethod = PasswordTransformationMethod.getInstance()
                ivInvisible.setImageDrawable(getDrawable(R.mipmap.icon_invisible))
            }
            edCreate.setSelection(edCreate.text.length)
        }

        edCreate.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val empty = edCreate.text.isNullOrEmpty()
                btnConfirm.isEnabled = !empty
                if (!empty)
                    btnConfirm.background = getDrawable(R.drawable.selector_verify_btn_bg)
                else
                    btnConfirm.background = getDrawable(R.drawable.shape_btn_bg_not_enabled)
            }

        })
        btnConfirm.isEnabled = !edCreate.text.isNullOrEmpty()
        btnConfirm.background = getDrawable(R.drawable.shape_btn_bg_not_enabled)
    }

}