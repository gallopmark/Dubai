package com.uroad.dubai.api.view

import com.uroad.dubai.api.BaseView
import com.uroad.dubai.model.GroupsInviteMDL

interface GroupsInviteView : BaseView {
    fun onGetInviteList(lastGroups: MutableList<GroupsInviteMDL.TeamMember>?,
                        followGroups: MutableList<GroupsInviteMDL.TeamMember>?)
}