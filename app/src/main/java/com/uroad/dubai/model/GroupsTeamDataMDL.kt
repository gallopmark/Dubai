package com.uroad.dubai.model

import com.mapbox.geojson.Point

class GroupsTeamDataMDL {
    var team_data: TeamData? = null
    var teammember: MutableList<TeamMember>? = null

    class TeamData {
        var teamid: String? = null
        var teamname: String? = null
        var toplace: String? = null
        var longitude: Double? = 0.0
        var latitude: Double? = 0.0
        var intoken: String? = null
        var token_text: String? = null
        fun point(): Point? {
            val longitude = this.longitude ?: return null
            val latitude = this.latitude ?: return null
            return Point.fromLngLat(longitude, latitude)
        }
    }

    class TeamMember {
        var useruuid: String? = null
        var isown: Int? = 0
        var longitude: Double? = 0.0
        var latitude: Double? = 0.0
        var username: String? = null
        var iconfile: String? = null
    }
}