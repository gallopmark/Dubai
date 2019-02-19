package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.uroad.library.common.BaseActivity
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.PersonalInformationPresenter
import com.uroad.dubai.api.view.PersonalInformationView
import com.uroad.dubai.dialog.EditDialog
import com.uroad.dubai.dialog.SexDialog
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.photopicker.utils.ImagePicker
import com.uroad.dubai.webService.WebApi
import com.uroad.glidev4.GlideV4
import kotlinx.android.synthetic.main.activity_personal_information.*

class PersonalInformationActivity : BaseActivity() ,PersonalInformationView{

    private lateinit var presenter: PersonalInformationPresenter
    private var uuID : String = ""
    private var nickName : String = ""
    private var avatar : String = ""
    private var sex : String = ""

    override fun setUp(savedInstanceState: Bundle?) {
        withTitle("Personal Information")
        setBaseContentView(R.layout.activity_personal_information)

        initView()
    }

    private fun initView(){
        presenter = PersonalInformationPresenter(this@PersonalInformationActivity)
        uuID  = UserPreferenceHelper.getUserUUID(this@PersonalInformationActivity)?:""
        nickName  = UserPreferenceHelper.getUserNickName(this@PersonalInformationActivity)?:""
        sex  = UserPreferenceHelper.getUserSex(this@PersonalInformationActivity)?:"0"
        avatar = UserPreferenceHelper.getAvatar(this@PersonalInformationActivity)?:""

        GlideV4.getInstance().displayImage(this@PersonalInformationActivity,
                avatar,
                ivAvatar,R.mipmap.ic_user_default)
        tvSex.text = sex(sex)
        tvNickname.setText(nickName)

        llAvatar.setOnClickListener {
            ImagePicker.with(this@PersonalInformationActivity)
                   .isCompress(true)
                   .requestCode(123)
                   .start()
        }

        llChange.setOnClickListener {
            openActivity(VerifyActivity::class.java,Bundle().apply {
                putString("phone",UserPreferenceHelper.getPhone(this@PersonalInformationActivity))
                putBoolean("forgot",true)
            })
        }

        llNickName.setOnClickListener {
            EditDialog(this@PersonalInformationActivity)
                    .withHint("Please enter a nickname")
                    .withButtonText("confirm")
                    .withButtonClickListener(object : EditDialog.OnButtonClickListener{
                        override fun onButtonClick(content: String, dialog: EditDialog) {
                            if (!content.isEmpty()){
                                dialog.dismiss()
                                nickName = content
                                tvNickname.setText(nickName)
                                presenter.update(WebApi.UPDATEPERSONALINFORMATION,WebApi.updatePersonalInformation(
                                        uuID, nickName, sex, avatar
                                ))
                                UserPreferenceHelper.saveUserNickname(this@PersonalInformationActivity,nickName)
                            }
                        }

                    })
                    .show()
        }

        llSex.setOnClickListener {
            SexDialog(this@PersonalInformationActivity)
                    .setPositiveButton(getString(R.string.confirm),object : SexDialog.OnClickListener{
                        override fun onClick(v: View, dialog: SexDialog) {
                            val s = dialog.getSex()
                            if (s == "0"){
                                dialog.dismiss()
                                return
                            }
                            sex = s
                            tvSex.text = sex(sex)
                            presenter.update(WebApi.UPDATEPERSONALINFORMATION,WebApi.updatePersonalInformation(
                                    uuID, nickName, sex, avatar
                            ))
                            UserPreferenceHelper.saveUserSex(this@PersonalInformationActivity,sex)
                            dialog.dismiss()
                        }

                    })
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == 123 && resultCode == Activity.RESULT_OK ){
            val images = data?.getStringArrayListExtra(ImagePicker.EXTRA_PATHS)
            images?.let {
                GlideV4.getInstance().displayImage(this@PersonalInformationActivity,
                        images[0],ivAvatar,R.mipmap.ic_user_default)
                presenter.doUploadAvatar(images[0])
            }
        }
    }

    private fun sex(code : String) = when(code){
        "1" -> "man"
        "2" -> "female"
        else -> "undefine"
    }

    @SuppressLint("LogNotTimber")
    override fun onSuccess(k1: String, k2: String) {
        avatar = k1
        UserPreferenceHelper.saveAvatar(this@PersonalInformationActivity,avatar)
        presenter.update(WebApi.UPDATEPERSONALINFORMATION,WebApi.updatePersonalInformation(
                uuID, nickName, sex, avatar
        ))
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
        showLongToast(msg)
    }
}