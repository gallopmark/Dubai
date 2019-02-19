package com.uroad.dubai.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.uroad.dubai.R
import com.uroad.dubai.adapter.GroupsInviteAdapter
import com.uroad.dubai.api.presenter.GroupsInvitePresenter
import com.uroad.dubai.api.view.GroupsInviteView
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.common.BasePresenterActivity
import com.uroad.dubai.model.GroupsInviteMDL
import com.uroad.dubai.model.MultiItem
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.activity_groupsinvite.*

/**
 * @author MFB
 * @create 2019/1/10
 * @describe inviting teams
 */
class GroupsInviteActivity : BasePresenterActivity<GroupsInvitePresenter>(), GroupsInviteView {
    private lateinit var mSelected: MutableList<GroupsInviteMDL.TeamMember>
    private lateinit var selectedAdapter: SelectedAdapter
    private lateinit var inviteList: MutableList<MultiItem>
    private lateinit var inviteAdapter: GroupsInviteAdapter

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_groupsinvite)
        withTitle(getString(R.string.groups_invite))
        withOption(getString(R.string.confirm), View.OnClickListener { whenConfirm() })
        initDataView()
    }

    private fun initDataView() {
        mSelected = ArrayList()
        selectedAdapter = SelectedAdapter(this, mSelected)
        rvSelected.adapter = selectedAdapter
        inviteList = ArrayList()
        inviteAdapter = GroupsInviteAdapter(this, inviteList).apply {
            setOnItemSelectedListener(object : GroupsInviteAdapter.OnItemSelectedListener {
                override fun onSelected(mSelected: MutableList<GroupsInviteMDL.TeamMember>) {
                    this@GroupsInviteActivity.mSelected.clear()
                    this@GroupsInviteActivity.mSelected.addAll(mSelected)
                    selectedAdapter.notifyDataSetChanged()
                    updateSelectedText()
                }
            })
        }
        rvList.adapter = inviteAdapter
    }

    private fun updateSelectedText() {
        var source = "${getString(R.string.selected)}\u3000"
        val start = source.length
        source += if (mSelected.size > 0) mSelected.size else ""
        tvSelected.text = SpannableString(source).apply { setSpan(ForegroundColorSpan(ContextCompat.getColor(this@GroupsInviteActivity, R.color.theme_red)), start, source.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }

    override fun createPresenter(): GroupsInvitePresenter = GroupsInvitePresenter(this)
    override fun initData() {
        presenter.getInviteList("", "")
    }

    override fun onShowLoading() {
        onPageLoading()
    }

    override fun onHideLoading() {
        onPageResponse()
    }

    override fun onGetInviteList(lastGroups: MutableList<GroupsInviteMDL.TeamMember>?, followGroups: MutableList<GroupsInviteMDL.TeamMember>?) {
        inviteList.clear()
        lastGroups?.let {
            if (it.size > 0) {
                inviteList.add(GroupsInviteMDL.GroupName().apply { name = getString(R.string.groups_lastGroup) })
                inviteList.addAll(it)
            }
        }
        followGroups?.let {
            if (it.size > 0) {
                inviteList.add(GroupsInviteMDL.GroupName().apply { name = getString(R.string.groups_myFollow) })
                inviteList.addAll(it)
            }
        }
        inviteAdapter.notifyDataSetChanged()
        if (inviteList.size > 0) {
            rvList.visibility = View.VISIBLE
        } else {
            rvList.visibility = View.GONE
            onPageNoData()
        }
    }

    private fun whenConfirm() {
        if (mSelected.size > 0) { //when selected do something

        } else {
            showLongToast(getString(R.string.groups_invite_unselected))
        }
    }

    private inner class SelectedAdapter(context: Context, data: MutableList<GroupsInviteMDL.TeamMember>)
        : BaseArrayRecyclerAdapter<GroupsInviteMDL.TeamMember>(context, data) {
        private val dp2 = DisplayUtils.dip2px(context, 2f)
        private val dp5 = DisplayUtils.dip2px(context, 5f)
        override fun bindView(viewType: Int): Int = R.layout.item_groupsselected

        override fun onBindHoder(holder: RecyclerHolder, t: GroupsInviteMDL.TeamMember, position: Int) {
            holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply { rightMargin = dp5 }
            holder.displayImage(R.id.ivPic, t.iconfile, R.color.color_f7, RoundedCorners(dp2))
        }
    }
}