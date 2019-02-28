package com.uroad.dubai.model

class GroupsStateMDL {
    var type: Int? = 0
    var content: MutableList<GroupsMsg>? = null

    class GroupsMsg {
        var toplace: String? = null
        var teamid: String? = null
        var longitude: Double? = 0.0
        var latitude: Double? = 0.0
        var intoken: String? = null
        var username: String? = null
    }
}