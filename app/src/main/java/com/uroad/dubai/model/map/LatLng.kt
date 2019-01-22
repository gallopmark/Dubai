package com.uroad.dubai.model.map

class LatLng {
    var longitude: Double? = null
    var latitude: Double? = null

    constructor()
    constructor(latitude: Double?, longitude: Double?) {
        this.latitude = latitude
        this.longitude = longitude
    }
}