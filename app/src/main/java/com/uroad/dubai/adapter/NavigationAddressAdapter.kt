package com.uroad.dubai.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.enumeration.AddressType
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.SettingAddressMDL
import com.uroad.dubai.model.UserAddressMDL

/**
 * @author MFB
 * @create 2019/1/24
 * @describe
 */
class NavigationAddressAdapter(private val context: Context,
                               private val data: MutableList<MultiItem>)
    : BaseArrayRecyclerAdapter<MultiItem>(context, data) {
    override fun getItemViewType(position: Int): Int {
        return data[position].getItemType()
    }

    override fun bindView(viewType: Int): Int =
            if (viewType == 1) R.layout.item_navigationaddress_default
            else R.layout.item_navigationaddress

    override fun onBindHoder(holder: RecyclerHolder, t: MultiItem, position: Int) {
        val itemType = holder.itemViewType
        if (itemType == 1) {
            val mdl = t as SettingAddressMDL
            holder.setImageResource(R.id.ivIcon, mdl.icon)
            holder.setText(R.id.tvName, mdl.name)
            holder.setText(R.id.tvTips, mdl.tips)
        } else {
            val mdl = t as UserAddressMDL
            if (TextUtils.equals(mdl.addresstype, AddressType.HOME.CODE)) {
                holder.setImageResource(R.id.ivIcon, R.mipmap.ic_route_home)
                holder.setText(R.id.tvName, context.getString(R.string.route_home))
            } else {
                holder.setImageResource(R.id.ivIcon, R.mipmap.ic_route_work)
                holder.setText(R.id.tvName, context.getString(R.string.route_work))
            }
            holder.setText(R.id.tvTips, mdl.address)
            holder.bindChildClick(R.id.ivDelete)
        }
        if (position == itemCount - 1) {
            holder.setVisibility(R.id.vLine, View.GONE)
        } else {
            holder.setVisibility(R.id.vLine, View.VISIBLE)
        }
    }
}