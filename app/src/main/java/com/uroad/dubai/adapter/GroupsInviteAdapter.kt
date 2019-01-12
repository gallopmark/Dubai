package com.uroad.dubai.adapter

import android.content.Context
import android.support.v4.util.ArrayMap
import android.view.View
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.GroupsInviteMDL
import com.uroad.dubai.model.MultiItem
import com.uroad.library.utils.DisplayUtils

class GroupsInviteAdapter(context: Context, private val data: MutableList<MultiItem>)
    : BaseArrayRecyclerAdapter<MultiItem>(context, data) {

    private val mSelected = ArrayList<GroupsInviteMDL.TeamMember>()
    private val dp4 = DisplayUtils.dip2px(context, 4f)
    private var onItemSelectedListener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    override fun getItemViewType(position: Int): Int = data[position].getItemType()

    override fun bindView(viewType: Int): Int {
        return if (viewType == 0) R.layout.item_groupsinvite_name
        else R.layout.item_groupsinvite_user
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MultiItem, position: Int) {
        val itemType = holder.itemViewType
        if (itemType == 0) {
            val m = t as GroupsInviteMDL.GroupName
            holder.setText(R.id.tvGroupName, m.name)
        } else {
            val m = t as GroupsInviteMDL.TeamMember
            if (m.isInvitation == 0) {
                holder.setImageResource(R.id.ivSelected, R.mipmap.ic_checkbox_unselected)
                holder.setVisibility(R.id.tvStatus, View.VISIBLE)
            } else {
                if (mSelected.contains(m)) {
                    holder.setImageResource(R.id.ivSelected, R.mipmap.ic_checkbox_checked)
                } else {
                    holder.setImageResource(R.id.ivSelected, R.mipmap.ic_checkbox_default)
                }
                holder.setVisibility(R.id.tvStatus, View.GONE)
            }
            holder.setText(R.id.tvName, m.username)
            holder.displayImage(R.id.ivPic, m.iconfile, R.color.color_f7, RoundedCorners(dp4))
            holder.itemView.setOnClickListener {
                if (m.isInvitation == 1) {
                    if (mSelected.contains(m)) {
                        mSelected.remove(m)
                        holder.setImageResource(R.id.ivSelected, R.mipmap.ic_checkbox_default)
                    } else {
                        mSelected.add(m)
                        holder.setImageResource(R.id.ivSelected, R.mipmap.ic_checkbox_checked)
                    }
                    onItemSelectedListener?.onSelected(mSelected)
                }
            }
        }
    }

    interface OnItemSelectedListener {
        fun onSelected(mSelected: MutableList<GroupsInviteMDL.TeamMember>)
    }
}
