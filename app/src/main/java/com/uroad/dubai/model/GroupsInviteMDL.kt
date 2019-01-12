package com.uroad.dubai.model

class GroupsInviteMDL {
    var lastteammember: MutableList<TeamMember>? = null
    var follow: MutableList<TeamMember>? = null

    class GroupName : MultiItem {
        override fun getItemType(): Int = 0
        var name: String? = null
    }

    class TeamMember : MultiItem {
        override fun getItemType(): Int = 1
        var userid: String? = null
        var username: String? = null
        var iconfile: String? = null
        var isInvitation: Int? = 0

        override fun equals(other: Any?): Boolean {
            return when (other) {
                !is TeamMember -> false
                else -> this === other || userid == other.userid
            }
        }

        override fun hashCode(): Int {
            return 31 + (userid?.hashCode() ?: 0)
        }
    }
}