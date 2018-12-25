package com.uroad.dubai.model

import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions

class CustomMarker(option: MarkerOptions) : Marker(option) {
    var `object`: Any? = null
}