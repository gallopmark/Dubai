package com.uroad.dubai.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.support.v4.content.ContextCompat
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.uroad.dubai.R
import com.uroad.dubai.enumeration.MapDataType
import com.uroad.dubai.enumeration.MapPointId

class MyMapUtils {
    companion object {
        private const val MY_LOCATION_SOURCE_ID = "my-location-source-id"
        private const val MY_LOCATION_IMAGE = "my-location-image"
        private const val MY_LOCATION_LAYER_ID = "my-location-layer-id"

        fun drawMyLocationMarker(style: Style?, location: Location, drawable: Drawable?) {
            val feature = Feature.fromGeometry(Point.fromLngLat(location.longitude, location.latitude))
            val geoJsonSource = GeoJsonSource(MY_LOCATION_SOURCE_ID, feature)
            drawable?.let { style?.addImage(MY_LOCATION_IMAGE, it) }
            style?.addSource(geoJsonSource)
            style?.addLayer(SymbolLayer(MY_LOCATION_LAYER_ID, MY_LOCATION_SOURCE_ID)
                    .withProperties(PropertyFactory.iconImage(MY_LOCATION_IMAGE),
                            PropertyFactory.iconIgnorePlacement(true),
                            PropertyFactory.iconAllowOverlap(true)))
        }

        fun clearMyLocationMarker(style: Style?) {
            style?.removeImage(MY_LOCATION_IMAGE)
            style?.removeLayer(MY_LOCATION_LAYER_ID)
            style?.removeSource(MY_LOCATION_SOURCE_ID)
        }

        fun animateCamera(mapBoxMap: MapboxMap?, target: LatLng, zoom: Double) {
            var mapZoom = zoom
            mapBoxMap?.cameraPosition?.let { mapZoom = it.zoom }
            val position = CameraPosition.Builder().target(target).zoom(mapZoom).build()
            mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        }

        fun addClusterPoint(context: Context, style: Style?, features: MutableList<Feature>, sourceId: String,
                            unClusterLayerId: String, clusterLayerId: String,
                            unClusterImage: String, clusterImage: String,
                            pointCountLayerId: String, drawable: Drawable?): MutableList<String> {
            addClusterImage(style, unClusterImage, clusterImage, drawable)
            val collection = FeatureCollection.fromFeatures(features)
            style?.addSource(GeoJsonSource(sourceId, collection, GeoJsonOptions().withCluster(true)))
            style?.addLayer(SymbolLayer(unClusterLayerId, sourceId)
                    .withProperties(PropertyFactory.iconImage(unClusterImage),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconIgnorePlacement(true)))
            val layers = intArrayOf(features.size, 10, 0)
            val clusterLayerIds = ArrayList<String>()
            for (i in layers.indices) {
                val layerId = "$clusterLayerId-$i"
                val symbolLayer = SymbolLayer(layerId, sourceId)
                        .withProperties(PropertyFactory.iconImage(clusterImage),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true))
                val pointCount = toNumber(get(MapPointId.POINT_COUNT.CODE))
                symbolLayer.setFilter(
                        if (i == 0)
                            all(has(MapPointId.POINT_COUNT.CODE), gte(pointCount, literal(layers[i])))
                        else
                            all(has(MapPointId.POINT_COUNT.CODE), gt(pointCount, literal(layers[i])),
                                    lt(pointCount, Expression.literal(layers[i - 1])))
                )
                clusterLayerIds.add(layerId)
                style?.addLayer(symbolLayer)
            }
            style?.addLayer(SymbolLayer(pointCountLayerId, sourceId).withProperties(
                    textField(Expression.toString(get(MapPointId.POINT_COUNT.CODE))),
                    textSize(12f),
                    textOffset(arrayOf(0.5f, -1f)),
                    textColor(ContextCompat.getColor(context, R.color.theme_red))))
            return clusterLayerIds
        }

        private fun addClusterImage(style: Style?, unClusterImage: String, clusterImage: String, drawable: Drawable?) {
            drawable?.let { style?.addImage(unClusterImage, it) }
            drawable?.let { style?.addImage(clusterImage, it) }
        }

        fun addSinglePoint(style: Style?, sourceId: String, feature: Feature,
                           layerId: String, imageName: String, drawable: Drawable?) {
            drawable?.let { style?.addImage(imageName, it) }
            val collection = FeatureCollection.fromFeature(feature)
            style?.addSource(GeoJsonSource(sourceId, collection))
            style?.addLayer(SymbolLayer(layerId, sourceId)
                    .withProperties(PropertyFactory.iconImage(imageName),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconIgnorePlacement(true)))
        }

        fun getMapPointSourceIdByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_SOURCE_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_SOURCE_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_SOURCE_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_SOURCE_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_SOURCE_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_SOURCE_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_SOURCE_ID.CODE
                else -> MapPointId.BUS_STOP_SOURCE_ID.CODE
            }
        }

        fun getMapPointUnClusterImageByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_UNCLUSTER_IMAGE_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_UNCLUSTER_IMAGE_ID.CODE
                else -> MapPointId.BUS_STOP_UNCLUSTER_IMAGE_ID.CODE
            }
        }

        fun getMapPointClusterImageByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_CLUSTER_IMAGE_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_CLUSTER_IMAGE_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_CLUSTER_IMAGE_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_CLUSTER_IMAGE_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_CLUSTER_IMAGE_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_CLUSTER_IMAGE_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_CLUSTER_IMAGE_ID.CODE
                else -> MapPointId.BUS_STOP_CLUSTER_IMAGE_ID.CODE
            }
        }

        fun getMapPointUnClusterLayerIdByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_UNCLUSTER_LAYER_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_UNCLUSTER_LAYER_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_UNCLUSTER_LAYER_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_UNCLUSTER_LAYER_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_UNCLUSTER_LAYER_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_UNCLUSTER_LAYER_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_UNCLUSTER_LAYER_ID.CODE
                else -> MapPointId.BUS_STOP_UNCLUSTER_LAYER_ID.CODE
            }
        }

        fun getMapPointClusterLayerIdByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_CLUSTER_LAYER_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_CLUSTER_LAYER_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_CLUSTER_LAYER_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_CLUSTER_LAYER_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_CLUSTER_LAYER_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_CLUSTER_LAYER_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_CLUSTER_LAYER_ID.CODE
                else -> MapPointId.BUS_STOP_CLUSTER_LAYER_ID.CODE
            }
        }

        fun getMapPointPropertyByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_PROPERTY.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_PROPERTY.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_PROPERTY.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_PROPERTY.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_PROPERTY.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_PROPERTY.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_PROPERTY.CODE
                else -> MapPointId.BUS_STOP_PROPERTY.CODE
            }
        }

        fun getMapPointDrawableByCode(context: Context, CODE: String): Drawable? {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_accident)
                MapDataType.PARKING.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_parking)
                MapDataType.CCTV.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_cctv)
                MapDataType.DMS.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_dms)
                MapDataType.POLICE.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_police)
                MapDataType.RWIS.CODE -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_rwis)
                else -> ContextCompat.getDrawable(context, R.mipmap.ic_marker_busstop)
            }
        }

        fun getMapPointCountLayerIdByCode(CODE: String): String {
            return when (CODE) {
                MapDataType.ACCIDENT.CODE -> MapPointId.EVENTS_COUNT_LAYER_ID.CODE
                MapDataType.PARKING.CODE -> MapPointId.PARKING_COUNT_LAYER_ID.CODE
                MapDataType.CCTV.CODE -> MapPointId.CCTV_COUNT_LAYER_ID.CODE
                MapDataType.DMS.CODE -> MapPointId.DMS_COUNT_LAYER_ID.CODE
                MapDataType.POLICE.CODE -> MapPointId.POLICE_COUNT_LAYER_ID.CODE
                MapDataType.WEATHER.CODE -> MapPointId.WEATHER_COUNT_LAYER_ID.CODE
                MapDataType.RWIS.CODE -> MapPointId.RWIS_COUNT_LAYER_ID.CODE
                else -> MapPointId.BUS_STOP_COUNT_LAYER_ID.CODE
            }
        }

        fun removeImage(style: Style?, imageId: String) {
            style?.removeImage(imageId)
        }

        fun removeLayer(style: Style?, layerId: String) {
            style?.removeLayer(layerId)
        }

        fun removeSource(style: Style?, sourceId: String) {
            style?.removeSource(sourceId)
        }
    }
}