package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.upload.UploadFileCallback
import com.uroad.dubai.api.view.PersonalInformationView
import com.uroad.http.upload.SingleFileUploadListener
import okhttp3.ResponseBody
import org.json.JSONObject

class PersonalInformationPresenter(var personalInformationView: PersonalInformationView) : BasePresenter<PersonalInformationView>(personalInformationView) {

    fun doUploadAvatar(url: String) {
        doUpload(url, object : SingleFileUploadListener {
            @Throws(Exception::class)
            override fun onSuccess(responseBody: ResponseBody) {
                val json = responseBody.string()
                val jsonObject = JSONObject(json)
                val data = jsonObject.optJSONObject("data")
                val imgurl = data.optJSONObject("imgurl")
                val k1 = imgurl.optString("file", "")
                val k2 = imgurl.optString("k2", "")
                personalInformationView.onSuccess(k1, k2)
            }

            override fun onFailure(e: Throwable) {
                personalInformationView.onShowError(e.toString())
            }

        })
    }

    fun update(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(personalInformationView) {
            override fun onHttpResultOk(data: String?) {
                val jsonObject = JSONObject(data)
                val data = jsonObject.optString("data")
                personalInformationView.onShowError(data)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                personalInformationView.onShowError(errorMsg)
            }

        })
    }
}