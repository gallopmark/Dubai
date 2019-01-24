package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.UserAddressMDL

/**
 * @author MFB
 * @create 2019/1/24
 * @describe
 */
interface UserAddressView : BaseView {
    fun onSuccess(data: String?, funType: Int)
    fun onGetUserAddress(data: MutableList<UserAddressMDL>)
    fun onFailure(errMsg:String?,errCode:Int?)
}