package com.uroad.dubai.model

class SettingAddressMDL :MultiItem{
    var icon: Int = 0
    var name: String? = null
    var tips: String? = null
    override fun getItemType(): Int = 1
}