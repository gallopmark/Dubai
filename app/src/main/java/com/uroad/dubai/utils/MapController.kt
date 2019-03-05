package com.uroad.dubai.utils

import android.content.Context
import android.support.v4.util.ArrayMap
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.uroad.dubai.model.MapPointItem

class MapController constructor(private val context: Context) {
    private val dataMap = ArrayMap<String, MutableList<MapPointItem>>()
    private val unClusterImageMap = ArrayMap<String, String>()
    private val clusterImageMap = ArrayMap<String, String>()
    private val unClusterLayerMap = ArrayMap<String, String>()
    private val clusterLayerMap = ArrayMap<String, MutableList<String>>()
    private val pointCountLayerMap = ArrayMap<String, String>()
    private val sourceMap = ArrayMap<String, String>()
    private val pointMap = ArrayMap<String, MapPointItem>()

    fun showPoint(style: Style?, code: String, data: MutableList<MapPointItem>) {
        val sourceId = MyMapUtils.getMapPointSourceIdByCode(code)
        val unClusterImage = MyMapUtils.getMapPointUnClusterImageByCode(code)
        val clusterImage = MyMapUtils.getMapPointClusterImageByCode(code)
        val unClusterLayerId = MyMapUtils.getMapPointUnClusterLayerIdByCode(code)
        val clusterLayerId = MyMapUtils.getMapPointClusterLayerIdByCode(code)
        val pointCountLayerId = MyMapUtils.getMapPointCountLayerIdByCode(code)
        val drawable = MyMapUtils.getMapPointDrawableByCode(context, code)
        val property = MyMapUtils.getMapPointPropertyByCode(code)
        val features = ArrayList<Feature>()
        for (i in data.indices) {
            val latLng = data[i].getLatLng()
            val stringProperty = "$property-$i"
            features.add(Feature.fromGeometry(Point.fromLngLat(latLng.longitude, latLng.latitude)).apply { addStringProperty("pointProperty", stringProperty) })
            pointMap[stringProperty] = data[i]
        }
        clusterLayerMap[code] = MyMapUtils.addClusterPoint(context, style, features, sourceId,
                unClusterLayerId, clusterLayerId,
                unClusterImage, clusterImage,
                pointCountLayerId, drawable
        )
        unClusterImageMap[code] = unClusterImage
        clusterImageMap[code] = clusterImage
        unClusterLayerMap[code] = unClusterLayerId
        pointCountLayerMap[code] = pointCountLayerId
        sourceMap[code] = sourceId
        dataMap[code] = data
    }

    fun removePoint(style: Style?, code: String) {
        unClusterImageMap[code]?.let {
            MyMapUtils.removeImage(style, it)
            unClusterImageMap.remove(code)
        }
        clusterImageMap[code]?.let {
            MyMapUtils.removeImage(style, it)
            clusterImageMap.remove(code)
        }
        unClusterLayerMap[code]?.let {
            MyMapUtils.removeLayer(style, it)
            unClusterLayerMap.remove(code)
        }
        clusterLayerMap[code]?.let {
            for (layer in it) MyMapUtils.removeLayer(style, layer)
            clusterLayerMap.remove(code)
        }
        pointCountLayerMap[code]?.let {
            MyMapUtils.removeLayer(style, it)
            pointCountLayerMap.remove(code)
        }
        sourceMap[code]?.let {
            MyMapUtils.removeSource(style, it)
            sourceMap.remove(code)
        }
        dataMap.remove(code)
    }

    fun removeAllWhenStyleChanged(style: Style?) {
        for ((_, v) in unClusterImageMap) MyMapUtils.removeImage(style, v)
        for ((_, v) in clusterImageMap) MyMapUtils.removeImage(style, v)
        for ((_, v) in unClusterLayerMap) MyMapUtils.removeImage(style, v)
        for ((_, v) in clusterLayerMap) {
            for (layer in v) MyMapUtils.removeImage(style, layer)
        }
        for ((_, v) in pointCountLayerMap) MyMapUtils.removeImage(style, v)
        for ((_, v) in sourceMap) MyMapUtils.removeImage(style, v)
    }

    fun dataMap() = dataMap
    fun pointMap() = pointMap

    fun enlargeMap(mapBoxMap: MapboxMap?) {
        mapBoxMap?.let {
            it.cameraPosition.apply {
                var mapZoom = zoom
                scaleMap(mapBoxMap, target, ++mapZoom)
            }
        }
    }

    fun narrowMap(mapBoxMap: MapboxMap?) {
        mapBoxMap?.let {
            it.cameraPosition.apply {
                var mapZoom = zoom
                scaleMap(mapBoxMap, target, --mapZoom)
            }
        }
    }

    fun scaleMap(mapBoxMap: MapboxMap?, nowLocation: LatLng, scaleValue: Double) {
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, scaleValue))
    }
}

