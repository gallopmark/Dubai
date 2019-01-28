package com.uroad.dubai.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.R
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.model.UserMDL
import com.uroad.dubai.photopicker.utils.ImagePicker
import com.uroad.glidev4.GlideV4
import kotlinx.android.synthetic.main.activity_personal_information.*

class PersonalInformationActivity : BaseActivity() {

    private var user : UserMDL? = DubaiApplication.user

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle("Personal Information")
        setBaseContentView(R.layout.activity_personal_information)

        initView()
    }

    private fun initView(){
        user?.let {
            GlideV4.getInstance().displayImage(this@PersonalInformationActivity,
                    it.headimg,ivAvatar,R.mipmap.ic_user_default)
            tvSex.text = sex("$it.sex")
            tvNickname.text = it.nickname
        }
        ivAvatar.setOnClickListener {
            ImagePicker.with(this@PersonalInformationActivity)
                   .isCompress(true)
                   .requestCode(123)
                   .start()
        }

        llChange.setOnClickListener {
            openActivity(VerifyActivity::class.java,Bundle().apply {
                putString("phone",user?.mobile)
                putBoolean("forgot",true)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == 123 && resultCode == Activity.RESULT_OK ){
            val images = data?.getStringArrayListExtra(ImagePicker.EXTRA_PATHS)
            images?.let {
                GlideV4.getInstance().displayImage(this@PersonalInformationActivity,
                        images[0],ivAvatar,R.mipmap.ic_user_default)
            }
        }
    }

    private fun sex(code : String) = when(code){
        "1" -> "man"
        "2" -> "female"
        else -> "undefine"
    }
}