package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.UserAddressView
import com.uroad.dubai.model.UserAddressMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class UserAddressPresenter(private val userAddressView: UserAddressView)
    : BasePresenter<UserAddressView>(userAddressView) {

    fun setUpUserAddress(useruuid: String?, address: String?
                         , lnglat: String?, addresstype: String?) {
        request(WebApi.SETUP_USER_ADDRESS, WebApi.setUpUserAddressParams("", useruuid, address, lnglat, addresstype), object : StringObserver(userAddressView) {
            override fun onHttpResultOk(data: String?) {
                userAddressView.onSuccess(GsonUtils.fromDataBean(data, UserAddressMDL::class.java)?.addressid, 1)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                userAddressView.onFailure(errorMsg, errorCode)
            }
        })
    }

    fun deleteUserAddress(addressId: String?) {
        request(WebApi.DELETE_USER_ADDRESS, WebApi.deleteUserAddressParams(addressId), object : StringObserver(userAddressView) {
            override fun onHttpResultOk(data: String?) {
                userAddressView.onSuccess(GsonUtils.getDataAsString(data), 2)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                userAddressView.onFailure(errorMsg, errorCode)
            }
        })
    }

    fun getUserAddress(useruuid: String?) {
        request(WebApi.GET_USER_ADDRESS, WebApi.getUserAddressParams(useruuid, ""), object : StringObserver(userAddressView) {
            override fun onHttpResultOk(data: String?) {
                userAddressView.onGetUserAddress(GsonUtils.fromDataToList(data, UserAddressMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                userAddressView.onFailure(errorMsg, errorCode)
            }
        })
    }
}