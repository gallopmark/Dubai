package com.uroad.dubai.model

import android.text.TextUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import com.uroad.dubai.enumeration.AddressType

class UserAddressMDL : MultiItem {
    var addressid: String? = null
    var address: String? = null
    var lnglat: String? = null
    var addresstype: String? = null

    override fun getItemType(): Int = 2

    fun getLatLng(): LatLng? {
        lnglat?.let {
            return try {
                val arr = it.split(",")
                LatLng(arr[1].toDouble(), arr[0].toDouble())
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getAddressType(): String {
        return when {
            TextUtils.equals(addresstype, "1") -> return AddressType.HOME.CODE
            TextUtils.equals(addresstype, "2") -> return AddressType.WORK.CODE
            else -> AddressType.HOME.CODE
        }
    }
}