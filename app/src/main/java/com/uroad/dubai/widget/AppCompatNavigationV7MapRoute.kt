//package com.uroad.dubai.widget
//
//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.LifecycleObserver
//import android.arch.lifecycle.OnLifecycleEvent
//import android.location.Location
//import android.support.annotation.*
//import android.support.v4.content.ContextCompat
//import android.support.v4.graphics.drawable.DrawableCompat
//import android.support.v7.content.res.AppCompatResources
//import android.text.TextUtils
//import com.mapbox.api.directions.v5.models.DirectionsRoute
//import com.mapbox.api.directions.v5.models.RouteLeg
//import com.mapbox.core.constants.Constants
//import com.mapbox.geojson.Feature
//import com.mapbox.geojson.FeatureCollection
//import com.mapbox.geojson.LineString
//import com.mapbox.geojson.Point
//import com.mapbox.mapboxsdk.annotations.IconFactory
//import com.mapbox.mapboxsdk.annotations.Marker
//import com.mapbox.mapboxsdk.annotations.MarkerOptions
//import com.mapbox.mapboxsdk.geometry.LatLng
//import com.mapbox.mapboxsdk.maps.MapView
//import com.mapbox.mapboxsdk.maps.MapboxMap
//import com.mapbox.mapboxsdk.maps.Style
//import com.mapbox.mapboxsdk.style.expressions.Expression
//import com.mapbox.mapboxsdk.style.expressions.Expression.*
//import com.mapbox.mapboxsdk.style.layers.*
//import com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_MAP
//import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
//import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
//import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
//import com.mapbox.mapboxsdk.utils.MathUtils
//import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
//import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener
//import com.mapbox.services.android.navigation.ui.v5.utils.MapImageUtils
//import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
//import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
//import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
//import com.mapbox.turf.TurfConstants
//import com.mapbox.turf.TurfMeasurement
//import com.mapbox.turf.TurfMisc
//import com.uroad.dubai.R
//import java.util.*
//
///**
// * @author MFB
// * @create 2018/12/28
// * @describe customNavigationMapRoute
// */
//class AppCompatNavigationMapRoute : LifecycleObserver {
//
//    companion object {
//        private const val CONGESTION_KEY = "congestion"
//        private const val SOURCE_KEY = "source"
//        private const val INDEX_KEY = "index"
//
//        private const val GENERIC_ROUTE_SOURCE_ID = "mapbox-navigation-route-source"
//        private const val GENERIC_ROUTE_LAYER_ID = "mapbox-navigation-route-layer"
//        private const val WAYPOINT_SOURCE_ID = "mapbox-navigation-waypoint-source"
//        private const val WAYPOINT_LAYER_ID = "mapbox-navigation-waypoint-layer"
//        private const val WAY_POINT_KEY = "wayPoint"
//        private const val ID_FORMAT = "%s-%d"
//        private const val GENERIC_ROUTE_SHIELD_LAYER_ID = "mapbox-navigation-route-shield-layer"
//        private const val TWO_POINTS = 2
//        private const val THIRTY = 30
//        private const val ARROW_BEARING = "mapbox-navigation-arrow-bearing"
//        private const val ARROW_SHAFT_SOURCE_ID = "mapbox-navigation-arrow-shaft-source"
//        private const val ARROW_HEAD_SOURCE_ID = "mapbox-navigation-arrow-head-source"
//        private const val ARROW_SHAFT_CASING_LINE_LAYER_ID = "mapbox-navigation-arrow-shaft-casing-layer"
//        private const val ARROW_SHAFT_LINE_LAYER_ID = "mapbox-navigation-arrow-shaft-layer"
//        private const val ARROW_HEAD_ICON = "mapbox-navigation-arrow-head-icon"
//        private const val ARROW_HEAD_ICON_CASING = "mapbox-navigation-arrow-head-icon-casing"
//        private const val MAX_DEGREES = 360
//        private const val ARROW_HEAD_CASING_LAYER_ID = "mapbox-navigation-arrow-head-casing-layer"
//        private val ARROW_HEAD_CASING_OFFSET = arrayOf(0f, -7f)
//        private const val ARROW_HEAD_LAYER_ID = "mapbox-navigation-arrow-head-layer"
//        private val ARROW_HEAD_OFFSET = arrayOf(0f, -7f)
//        private const val MIN_ARROW_ZOOM = 10
//        private const val MAX_ARROW_ZOOM = 22
//        private const val MIN_ZOOM_ARROW_SHAFT_SCALE = 2.6f
//        private const val MAX_ZOOM_ARROW_SHAFT_SCALE = 13.0f
//        private const val MIN_ZOOM_ARROW_SHAFT_CASING_SCALE = 3.4f
//        private const val MAX_ZOOM_ARROW_SHAFT_CASING_SCALE = 17.0f
//        private const val MIN_ZOOM_ARROW_HEAD_SCALE = 0.2f
//        private const val MAX_ZOOM_ARROW_HEAD_SCALE = 0.8f
//        private const val MIN_ZOOM_ARROW_HEAD_CASING_SCALE = 0.2f
//        private const val MAX_ZOOM_ARROW_HEAD_CASING_SCALE = 0.8f
//        private const val OPAQUE = 0.0f
//        private const val ARROW_HIDDEN_ZOOM_LEVEL = 14
//        private const val TRANSPARENT = 1.0f
//        private const val LAYER_ABOVE_UPCOMING_MANEUVER_ARROW = "com.mapbox.annotations.points"
//    }
//
//    @StyleRes
//    private var styleRes: Int = R.style.CustomNavigationMapRoute
//    @ColorInt
//    private var routeDefaultColor: Int = 0
//    @ColorInt
//    private var routeModerateColor: Int = 0
//    @ColorInt
//    private var routeSevereColor: Int = 0
//    @ColorInt
//    private var alternativeRouteDefaultColor: Int = 0
//    @ColorInt
//    private var alternativeRouteModerateColor: Int = 0
//    @ColorInt
//    private var alternativeRouteSevereColor: Int = 0
//    @ColorInt
//    private var alternativeRouteShieldColor: Int = 0
//    @ColorInt
//    private var routeShieldColor: Int = 0
//    @ColorInt
//    private var arrowColor: Int = 0
//    @ColorInt
//    private var arrowBorderColor: Int = 0
//    @DrawableRes
//    private var originWayPointIcon: Int = 0
//    @DrawableRes
//    private var destinationWayPointIcon: Int = 0
//
//    private var routeWidth = 10f
//    private var routeShieldLineWidth = 12f
//
//    private var navigation: MapboxNavigation? = null
//    private var mapboxMap: MapboxMap
//    private var routeLineStrings: HashMap<LineString, DirectionsRoute>
//    private var featureCollections: MutableList<FeatureCollection>
//    private var directionsRoutes: MutableList<DirectionsRoute>
//    private var layerIds: MutableList<String>
//    private var mapView: MapView
//    private var style: Style? = null
//    private var primaryRouteIndex: Int = 0
//    //    private float routeScale;
//    //    private float alternativeRouteScale;
//    private var belowLayer: String? = null
//    private var alternativesVisible: Boolean = false
//    private var onRouteSelectionChangeListener: OnRouteSelectionChangeListener? = null
//    private var arrowLayers: MutableList<Layer>? = null
//    private var arrowShaftGeoJsonSource: GeoJsonSource? = null
//    private var arrowHeadGeoJsonSource: GeoJsonSource? = null
//    private var arrowShaftGeoJsonFeature = Feature.fromGeometry(Point.fromLngLat(0.0, 0.0))
//    private var arrowHeadGeoJsonFeature = Feature.fromGeometry(Point.fromLngLat(0.0, 0.0))
//    private val progressChangeListener = MapRouteProgressChangeListener(this)
//    private lateinit var mapClickListener: MapboxMap.OnMapClickListener
//    private var isMapClickListenerAdded = false
//    private var didFinishLoadingStyleListener: MapView.OnDidFinishLoadingStyleListener? = null
//    private var isDidFinishLoadingStyleListenerAdded = false
//
//    private var originMarker: Marker? = null
//    private var destinationMarker: Marker? = null
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param mapView   the MapView to apply the route to
//     * @param mapboxMap the MapboxMap to apply route with
//     * @since 0.4.0
//     */
//    constructor(mapView: MapView, mapboxMap: MapboxMap) : this(null, mapView, mapboxMap, R.style.CustomNavigationMapRoute)
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param mapView    the MapView to apply the route to
//     * @param mapboxMap  the MapboxMap to apply route with
//     * @param belowLayer optionally pass in a layer id to place the route line below
//     * @since 0.4.0
//     */
//    constructor(mapView: MapView, mapboxMap: MapboxMap, belowLayer: String?) : this(null, mapView, mapboxMap, R.style.CustomNavigationMapRoute, belowLayer)
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param navigation an instance of the [MapboxNavigation] object. Passing in null means
//     * your route won't consider rerouting during a navigation session.
//     * @param mapView    the MapView to apply the route to
//     * @param mapboxMap  the MapboxMap to apply route with
//     * @since 0.4.0
//     */
//    constructor(navigation: MapboxNavigation?, mapView: MapView, mapboxMap: MapboxMap) : this(navigation, mapView, mapboxMap, R.style.CustomNavigationMapRoute)
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param navigation an instance of the [MapboxNavigation] object. Passing in null means
//     * your route won't consider rerouting during a navigation session.
//     * @param mapView    the MapView to apply the route to
//     * @param mapboxMap  the MapboxMap to apply route with
//     * @param belowLayer optionally pass in a layer id to place the route line below
//     * @since 0.4.0
//     */
//    constructor(navigation: MapboxNavigation?, mapView: MapView, mapboxMap: MapboxMap, belowLayer: String?) : this(navigation, mapView, mapboxMap, R.style.CustomNavigationMapRoute, belowLayer)
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param navigation an instance of the [MapboxNavigation] object. Passing in null means
//     * your route won't consider rerouting during a navigation session.
//     * @param mapView    the MapView to apply the route to
//     * @param mapboxMap  the MapboxMap to apply route with
//     * @param styleRes   a style resource with custom route colors, scale, etc.
//     */
//    constructor(navigation: MapboxNavigation?, mapView: MapView, mapboxMap: MapboxMap, @StyleRes styleRes: Int) : this(navigation, mapView, mapboxMap, styleRes, null)
//
//    /**
//     * Construct an instance of [NavigationMapRoute].
//     *
//     * @param navigation an instance of the [MapboxNavigation] object. Passing in null means
//     * your route won't consider rerouting during a navigation session.
//     * @param mapView    the MapView to apply the route to
//     * @param mapboxMap  the MapboxMap to apply route with
//     * @param styleRes   a style resource with custom route colors, scale, etc.
//     * @param belowLayer optionally pass in a layer id to place the route line below
//     */
//    constructor(navigation: MapboxNavigation?, mapView: MapView,
//                mapboxMap: MapboxMap, @StyleRes styleRes: Int,
//                belowLayer: String?) {
//        this.styleRes = styleRes
//        this.mapView = mapView
//        this.mapboxMap = mapboxMap
//        this.style = mapboxMap.style
//        this.navigation = navigation
//        this.belowLayer = belowLayer
//        featureCollections = ArrayList()
//        directionsRoutes = ArrayList()
//        routeLineStrings = HashMap()
//        layerIds = ArrayList()
//        initialize()
//        addListeners()
//    }
//
//    /**
//     * Allows adding a single primary route for the user to traverse along. No alternative routes will
//     * be drawn on top of the map.
//     *
//     * @param directionsRoute the directions route which you'd like to display on the map
//     * @since 0.4.0
//     */
//    fun addRoute(directionsRoute: DirectionsRoute) {
//        val routes = ArrayList<DirectionsRoute>()
//        routes.add(directionsRoute)
//        addRoutes(routes)
//    }
//
//    /**
//     * Provide a list of [DirectionsRoute]s, the primary route will default to the first route
//     * in the directions route list. All other routes in the list will be drawn on the map using the
//     * alternative route style.
//     *
//     * @param directionsRoutes a list of direction routes, first one being the primary and the rest of
//     * the routes are considered alternatives.
//     * @since 0.8.0
//     */
//    fun addRoutes(@Size(min = 1) directionsRoutes: List<DirectionsRoute>) {
//        clearRoutes()
//        this.directionsRoutes.addAll(directionsRoutes)
//        primaryRouteIndex = 0
//        alternativesVisible = directionsRoutes.size > 1
//        generateFeatureCollectionList(directionsRoutes)
//        drawRoutes()
//        addDirectionWayPoints()
//    }
//
//    /**
//     * Add a [OnRouteSelectionChangeListener] to know which route the user has currently
//     * selected as their primary route.
//     *
//     * @param onRouteSelectionChangeListener a listener which lets you know when the user has changed
//     * the primary route and provides the current direction
//     * route which the user has selected
//     * @since 0.8.0
//     */
//    fun setOnRouteSelectionChangeListener(onRouteSelectionChangeListener: OnRouteSelectionChangeListener?) {
//        this.onRouteSelectionChangeListener = onRouteSelectionChangeListener
//    }
//
//    /**
//     * Toggle whether or not you'd like the map to display the alternative routes. This options great
//     * for when the user actually begins the navigation session and alternative routes aren't needed
//     * anymore.
//     *
//     * @param alternativesVisible true if you'd like alternative routes to be displayed on the map,
//     * else false
//     * @since 0.8.0
//     */
//    private fun showAlternativeRoutes(alternativesVisible: Boolean) {
//        this.alternativesVisible = alternativesVisible
//        toggleAlternativeVisibility(alternativesVisible)
//    }
//
//    fun addProgressChangeListener(navigation: MapboxNavigation) {
//        this.navigation = navigation
//        navigation.addProgressChangeListener(progressChangeListener)
//    }
//
//    fun removeProgressChangeListener(navigation: MapboxNavigation?) {
//        navigation?.removeProgressChangeListener(progressChangeListener)
//    }
//
//    private fun addUpcomingManeuverArrow(routeProgress: RouteProgress) {
//        val points = routeProgress.upcomingStepPoints()
//        val invalidUpcomingStepPoints = points == null || points.size < TWO_POINTS
//        val invalidCurrentStepPoints = routeProgress.currentStepPoints().size < TWO_POINTS
//        if (invalidUpcomingStepPoints || invalidCurrentStepPoints) {
//            updateArrowLayersVisibilityTo(false)
//            return
//        }
//        updateArrowLayersVisibilityTo(true)
//
//        val maneuverPoints = obtainArrowPointsFrom(routeProgress)
//
//        updateArrowShaftWith(maneuverPoints)
//        updateArrowHeadWith(maneuverPoints)
//    }
//
//    private fun retrieveDirectionsRoutes(): List<DirectionsRoute> {
//        return directionsRoutes
//    }
//
//    private fun retrievePrimaryRouteIndex(): Int {
//        return primaryRouteIndex
//    }
//
//    //
//    // Private methods
//    //
//
//    /**
//     * Loops through all the route layers stored inside the layerId list and toggles the visibility.
//     * if the layerId matches the primary route index, we skip since we still want that route to be
//     * displayed.
//     */
//    private fun toggleAlternativeVisibility(visible: Boolean) {
//        for (layerId in layerIds) {
//            if (layerId.contains(primaryRouteIndex.toString()) || layerId.contains(WAYPOINT_LAYER_ID)) {
//                continue
//            }
//            val layer = style?.getLayer(layerId)
//            layer?.setProperties(visibility(if (visible) Property.VISIBLE else Property.NONE))
//        }
//    }
//
//    /**
//     * Takes the directions route list and draws each line on the map.
//     */
//    private fun drawRoutes() {
//        // Add all the sources, the list is traversed backwards to ensure the primary route always gets
//        // drawn on top of the others since it initially has a index of zero.
//        for (i in featureCollections.indices.reversed()) {
//            featureCollections[i].features()?.let {
//                if (it.size > 0) {
//                    val feature = it[0]
//                    updateMapSourceFromFeatureCollection(featureCollections[i], feature.getStringProperty(SOURCE_KEY))
//                    val sourceId = feature.getStringProperty(SOURCE_KEY)
//                    // Get some required information for the next step
//                    val index = featureCollections.indexOf(featureCollections[i])
//
//                    // Add the layer IDs to a list so we can quickly remove them when needed without traversing
//                    // through all the map layers.
//                    layerIds.add(String.format(Locale.US, ID_FORMAT, GENERIC_ROUTE_SHIELD_LAYER_ID, index))
//                    layerIds.add(String.format(Locale.US, ID_FORMAT, GENERIC_ROUTE_LAYER_ID, index))
//
//                    // Add the route shield first followed by the route to ensure the shield is always on the
//                    // bottom.
//                    addRouteShieldLayer(layerIds[layerIds.size - 2], sourceId, index)
//                    addRouteLayer(layerIds[layerIds.size - 1], sourceId, index)
//                }
//            }
//
//        }
//        progressChangeListener.updateVisibility(true)
//    }
//
//    private fun clearRoutes() {
//        removeLayerIds()
//        clearRouteListData()
//        updateArrowLayersVisibilityTo(false)
//        progressChangeListener.updateVisibility(false)
//    }
//
//    private fun generateFeatureCollectionList(directionsRoutes: List<DirectionsRoute>) {
//        // Each route contains traffic information and should be recreated considering this traffic
//        // information.
//        for (i in directionsRoutes.indices) {
//            featureCollections.add(addTrafficToSource(directionsRoutes[i], i))
//        }
//
//        // Add the waypoint geometries to represent them as an icon
//        featureCollections.add(wayPointFeatureCollection(directionsRoutes[primaryRouteIndex]))
//    }
//
//    /**
//     * The routes also display an icon for each waypoint in the route, we use symbol layers for this.
//     */
//    private fun wayPointFeatureCollection(route: DirectionsRoute): FeatureCollection {
//        val wayPointFeatures = ArrayList<Feature>()
//        route.legs()?.let { legs ->
//            for (leg in legs) {
//                getPointFromLineString(leg, 0)?.let { wayPointFeatures.add(it) }
//                getPointFromLineString(leg, legs.size - 1)?.let { wayPointFeatures.add(it) }
//            }
//        }
//        return FeatureCollection.fromFeatures(wayPointFeatures)
//    }
//
//    private fun addDirectionWayPoints() {
//        var wayPointFeatureCollection: FeatureCollection? = null
//        if (!featureCollections.isEmpty()) {
//            wayPointFeatureCollection = featureCollections[featureCollections.size - 1]
//        }
//        wayPointFeatureCollection?.let { updateMapSourceFromFeatureCollection(it, WAYPOINT_SOURCE_ID) }
//        drawWayPointMarkers()
//    }
//
//    private fun updateArrowLayersVisibilityTo(visible: Boolean) {
//        arrowLayers?.let {
//            for (layer in it) {
//                val targetVisibility = if (visible) Property.VISIBLE else Property.NONE
//                if (targetVisibility != layer.visibility.getValue()) {
//                    layer.setProperties(visibility(targetVisibility))
//                }
//            }
//        }
//    }
//
//    private fun obtainArrowPointsFrom(routeProgress: RouteProgress): List<Point> {
//        val reversedCurrent = ArrayList(routeProgress.currentStepPoints())
//        reversedCurrent.reverse()
//        val combined = ArrayList<Point>()
//        val arrowLineCurrent = LineString.fromLngLats(reversedCurrent)
//
//        val arrowCurrentSliced = TurfMisc.lineSliceAlong(arrowLineCurrent, 0.0, THIRTY.toDouble(), TurfConstants.UNIT_METERS)
//
//        arrowCurrentSliced.coordinates().reverse()
//        combined.addAll(arrowCurrentSliced.coordinates())
//        routeProgress.upcomingStepPoints()?.let {
//            val arrowLineUpcoming = LineString.fromLngLats(it)
//            val arrowUpcomingSliced = TurfMisc.lineSliceAlong(arrowLineUpcoming, 0.0, THIRTY.toDouble(), TurfConstants.UNIT_METERS)
//            combined.addAll(arrowUpcomingSliced.coordinates())
//        }
//        return combined
//    }
//
//    private fun updateArrowShaftWith(points: List<Point>) {
//        val shaft = LineString.fromLngLats(points)
//        arrowShaftGeoJsonFeature = Feature.fromGeometry(shaft)
//        arrowShaftGeoJsonSource?.setGeoJson(arrowShaftGeoJsonFeature)
//    }
//
//    private fun updateArrowHeadWith(points: List<Point>) {
//        val azimuth = TurfMeasurement.bearing(points[points.size - 2], points[points.size - 1])
//        arrowHeadGeoJsonFeature = Feature.fromGeometry(points[points.size - 1])
//        arrowHeadGeoJsonFeature.addNumberProperty(ARROW_BEARING, MathUtils.wrap(azimuth, 0.0, MAX_DEGREES.toDouble()).toFloat())
//        arrowHeadGeoJsonSource?.setGeoJson(arrowHeadGeoJsonFeature)
//    }
//
//    private fun initializeUpcomingManeuverArrow() {
//        arrowShaftGeoJsonSource = style?.getSource(ARROW_SHAFT_SOURCE_ID) as GeoJsonSource?
//        arrowHeadGeoJsonSource = style?.getSource(ARROW_HEAD_SOURCE_ID) as GeoJsonSource?
//
//        val shaftLayer = createArrowShaftLayer()
//        val shaftCasingLayer = createArrowShaftCasingLayer()
//        val headLayer = createArrowHeadLayer()
//        val headCasingLayer = createArrowHeadCasingLayer()
//
//        if (arrowShaftGeoJsonSource == null && arrowHeadGeoJsonSource == null) {
//            initializeArrowShaft()
//            initializeArrowHead()
//
//            addArrowHeadIcon()
//            addArrowHeadIconCasing()
//
//            style?.addLayerBelow(shaftCasingLayer, LAYER_ABOVE_UPCOMING_MANEUVER_ARROW)
//            style?.addLayerAbove(headCasingLayer, shaftCasingLayer.id)
//
//            style?.addLayerAbove(shaftLayer, headCasingLayer.id)
//            style?.addLayerAbove(headLayer, shaftLayer.id)
//        }
//        initializeArrowLayers(shaftLayer, shaftCasingLayer, headLayer, headCasingLayer)
//    }
//
//    private fun initializeArrowShaft() {
//        arrowShaftGeoJsonSource = GeoJsonSource(
//                ARROW_SHAFT_SOURCE_ID,
//                arrowShaftGeoJsonFeature,
//                GeoJsonOptions().withMaxZoom(16)
//        ).apply { style?.addSource(this) }
//    }
//
//    private fun initializeArrowHead() {
//        arrowHeadGeoJsonSource = GeoJsonSource(
//                ARROW_HEAD_SOURCE_ID,
//                arrowShaftGeoJsonFeature,
//                GeoJsonOptions().withMaxZoom(16)
//        ).apply { style?.addSource(this) }
//    }
//
//    private fun addArrowHeadIcon() {
//        AppCompatResources.getDrawable(mapView.context, R.drawable.ic_arrow_head)?.let {
//            val head = DrawableCompat.wrap(it)
//            DrawableCompat.setTint(head.mutate(), arrowColor)
//            val icon = MapImageUtils.getBitmapFromDrawable(head)
//            style?.addImage(ARROW_HEAD_ICON, icon)
//        }
//    }
//
//    private fun addArrowHeadIconCasing() {
//        AppCompatResources.getDrawable(mapView.context, R.drawable.ic_arrow_head_casing)?.let {
//            val headCasing = DrawableCompat.wrap(it)
//            DrawableCompat.setTint(headCasing.mutate(), arrowBorderColor)
//            val icon = MapImageUtils.getBitmapFromDrawable(headCasing)
//            style?.addImage(ARROW_HEAD_ICON_CASING, icon)
//        }
//    }
//
//    private fun createArrowShaftLayer(): LineLayer {
//        val shaftLayer = style?.getLayer(ARROW_SHAFT_LINE_LAYER_ID) as LineLayer?
//        return shaftLayer
//                ?: LineLayer(ARROW_SHAFT_LINE_LAYER_ID, ARROW_SHAFT_SOURCE_ID).withProperties(
//                        PropertyFactory.lineColor(color(arrowColor)),
//                        PropertyFactory.lineWidth(
//                                interpolate(linear(), zoom(),
//                                        stop(MIN_ARROW_ZOOM, MIN_ZOOM_ARROW_SHAFT_SCALE),
//                                        stop(MAX_ARROW_ZOOM, MAX_ZOOM_ARROW_SHAFT_SCALE)
//                                )
//                        ),
//                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                        PropertyFactory.visibility(Property.NONE),
//                        PropertyFactory.lineOpacity(step(zoom(), OPAQUE, stop(ARROW_HIDDEN_ZOOM_LEVEL, TRANSPARENT)))
//                )
//    }
//
//    private fun createArrowShaftCasingLayer(): LineLayer {
//        val shaftCasingLayer = style?.getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID) as LineLayer?
//        return shaftCasingLayer
//                ?: LineLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID, ARROW_SHAFT_SOURCE_ID).withProperties(
//                        PropertyFactory.lineColor(color(arrowBorderColor)),
//                        PropertyFactory.lineWidth(
//                                interpolate(linear(), zoom(),
//                                        stop(MIN_ARROW_ZOOM, MIN_ZOOM_ARROW_SHAFT_CASING_SCALE),
//                                        stop(MAX_ARROW_ZOOM, MAX_ZOOM_ARROW_SHAFT_CASING_SCALE)
//                                )
//                        ),
//                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                        PropertyFactory.visibility(Property.NONE),
//                        PropertyFactory.lineOpacity(step(zoom(), OPAQUE, stop(ARROW_HIDDEN_ZOOM_LEVEL, TRANSPARENT)))
//                )
//    }
//
//    private fun createArrowHeadLayer(): SymbolLayer {
//        val headLayer = style?.getLayer(ARROW_HEAD_LAYER_ID) as SymbolLayer?
//        return headLayer ?: SymbolLayer(ARROW_HEAD_LAYER_ID, ARROW_HEAD_SOURCE_ID)
//                .withProperties(
//                        PropertyFactory.iconImage(ARROW_HEAD_ICON),
//                        iconAllowOverlap(true),
//                        iconIgnorePlacement(true),
//                        PropertyFactory.iconSize(interpolate(linear(), zoom(),
//                                stop(MIN_ARROW_ZOOM, MIN_ZOOM_ARROW_HEAD_SCALE),
//                                stop(MAX_ARROW_ZOOM, MAX_ZOOM_ARROW_HEAD_SCALE))
//                        ),
//                        PropertyFactory.iconOffset(ARROW_HEAD_OFFSET),
//                        PropertyFactory.iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
//                        PropertyFactory.iconRotate(get(ARROW_BEARING)),
//                        PropertyFactory.visibility(Property.NONE),
//                        PropertyFactory.iconOpacity(step(zoom(), OPAQUE, stop(ARROW_HIDDEN_ZOOM_LEVEL, TRANSPARENT)))
//                )
//    }
//
//    private fun createArrowHeadCasingLayer(): SymbolLayer {
//        val headCasingLayer = style?.getLayer(ARROW_HEAD_CASING_LAYER_ID) as SymbolLayer?
//        return headCasingLayer
//                ?: SymbolLayer(ARROW_HEAD_CASING_LAYER_ID, ARROW_HEAD_SOURCE_ID).withProperties(
//                        PropertyFactory.iconImage(ARROW_HEAD_ICON_CASING),
//                        iconAllowOverlap(true),
//                        iconIgnorePlacement(true),
//                        PropertyFactory.iconSize(interpolate(
//                                linear(), zoom(),
//                                stop(MIN_ARROW_ZOOM, MIN_ZOOM_ARROW_HEAD_CASING_SCALE),
//                                stop(MAX_ARROW_ZOOM, MAX_ZOOM_ARROW_HEAD_CASING_SCALE)
//                        )),
//                        PropertyFactory.iconOffset(ARROW_HEAD_CASING_OFFSET),
//                        PropertyFactory.iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
//                        PropertyFactory.iconRotate(get(ARROW_BEARING)),
//                        PropertyFactory.visibility(Property.NONE),
//                        PropertyFactory.iconOpacity(step(zoom(), OPAQUE, stop(ARROW_HIDDEN_ZOOM_LEVEL, TRANSPARENT))
//                        )
//                )
//    }
//
//    private fun initializeArrowLayers(shaftLayer: LineLayer, shaftCasingLayer: LineLayer, headLayer: SymbolLayer,
//                                      headCasingLayer: SymbolLayer) {
//        arrowLayers = ArrayList<Layer>().apply {
//            add(shaftCasingLayer)
//            add(shaftLayer)
//            add(headCasingLayer)
//            add(headLayer)
//        }
//    }
//
//    /**
//     * When the user switches an alternative route to a primary route, this method alters the
//     * appearance.
//     */
//    private fun updatePrimaryRoute(layerId: String, index: Int) {
//        val layer = style?.getLayer(layerId)
//        layer?.let {
//            it.setProperties(PropertyFactory.lineColor(match(
//                    Expression.toString(get(CONGESTION_KEY)),
//                    color(if (index == primaryRouteIndex) routeDefaultColor else alternativeRouteDefaultColor),
//                    stop("moderate", color(if (index == primaryRouteIndex) routeModerateColor else alternativeRouteModerateColor)),
//                    stop("heavy", color(if (index == primaryRouteIndex) routeSevereColor else alternativeRouteSevereColor)),
//                    stop("severe", color(if (index == primaryRouteIndex) routeSevereColor else alternativeRouteSevereColor)))))
//            if (index == primaryRouteIndex) {
//                style?.removeLayer(it)
//                style?.addLayerBelow(it, WAYPOINT_LAYER_ID)
//            }
//        }
//    }
//
//    private fun updatePrimaryShieldRoute(layerId: String, index: Int) {
//        val layer = style?.getLayer(layerId)
//        layer?.let {
//            it.setProperties(PropertyFactory.lineColor(if (index == primaryRouteIndex) routeShieldColor else alternativeRouteShieldColor))
//            if (index == primaryRouteIndex) {
//                style?.removeLayer(it)
//                style?.addLayerBelow(it, WAYPOINT_LAYER_ID)
//            }
//        }
//    }
//
//    /**
//     * Add the route layer to the map either using the custom style values or the default.
//     */
//    private fun addRouteLayer(layerId: String, sourceId: String, index: Int) {
//        //        float scale = index == primaryRouteIndex ? routeScale : alternativeRouteScale;
//        val routeLayer = LineLayer(layerId, sourceId).withProperties(
//                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                PropertyFactory.lineWidth(routeWidth),
//                //                PropertyFactory.lineWidth(interpolate(
//                //                        exponential(1.5f), zoom(),
//                //                        stop(4f, 3f * scale),
//                //                        stop(10f, 4f * scale),
//                //                        stop(13f, 6f * scale),
//                //                        stop(16f, 10f * scale),
//                //                        stop(19f, 14f * scale),
//                //                        stop(22f, 18f * scale)
//                //                        )
//                //                ),
//                PropertyFactory.lineColor(match(
//                        Expression.toString(get(CONGESTION_KEY)),
//                        color(if (index == primaryRouteIndex) routeDefaultColor else alternativeRouteDefaultColor),
//                        stop("moderate", color(if (index == primaryRouteIndex) routeModerateColor else alternativeRouteModerateColor)),
//                        stop("heavy", color(if (index == primaryRouteIndex) routeSevereColor else alternativeRouteSevereColor)),
//                        stop("severe", color(if (index == primaryRouteIndex) routeSevereColor else alternativeRouteSevereColor))
//                )
//                )
//        )
//        addLayerToMap(routeLayer, belowLayer)
//    }
//
//    private fun removeLayerIds() {
//        if (!layerIds.isEmpty()) {
//            for (id in layerIds) {
//                style?.removeLayer(id)
//            }
//        }
//    }
//
//    private fun clearRouteListData() {
//        if (!directionsRoutes.isEmpty()) {
//            directionsRoutes.clear()
//        }
//        if (!routeLineStrings.isEmpty()) {
//            routeLineStrings.clear()
//        }
//        if (!featureCollections.isEmpty()) {
//            featureCollections.clear()
//        }
//    }
//
//    /**
//     * Add the route shield layer to the map either using the custom style values or the default.
//     */
//    private fun addRouteShieldLayer(layerId: String, sourceId: String, index: Int) {
//        //        float scale = index == primaryRouteIndex ? routeScale : alternativeRouteScale;
//        val routeLayer = LineLayer(layerId, sourceId).withProperties(
//                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                PropertyFactory.lineWidth(routeShieldLineWidth),
//                //                PropertyFactory.lineWidth(interpolate(
//                //                        exponential(1.5f), zoom(),
//                //                        stop(10f, 7f),
//                //                        stop(14f, 10.5f * scale),
//                //                        stop(16.5f, 15.5f * scale),
//                //                        stop(19f, 24f * scale),
//                //                        stop(22f, 29f * scale)
//                //                        )
//                //                ),
//                PropertyFactory.lineColor(if (index == primaryRouteIndex) routeShieldColor else alternativeRouteShieldColor)
//        )
//        addLayerToMap(routeLayer, belowLayer)
//    }
//
//    private fun addLayerToMap(@NonNull layer: Layer, @Nullable idBelowLayer: String?) {
//        if (style?.getLayer(layer.id) != null) {
//            return
//        }
//        if (idBelowLayer == null) {
//            style?.addLayer(layer)
//        } else {
//            style?.addLayerBelow(layer, idBelowLayer)
//        }
//    }
//
//    /**
//     * Loads in all the custom values the user might have set such as colors and line width scalars.
//     * Anything they didn't set, results in using the default values.
//     */
//    private fun getAttributes() {
//        val context = mapView.context
//        val typedArray = context.obtainStyledAttributes(styleRes, R.styleable.CustomNavigationMapRoute)
//
//        // Primary Route attributes
//        routeDefaultColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_routeColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_layer_blue))
//        routeModerateColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_routeModerateCongestionColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_layer_congestion_yellow))
//        routeSevereColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_routeSevereCongestionColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_layer_congestion_red))
//        routeShieldColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_routeShieldColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_shield_layer_color))
//        //        routeScale = typedArray.getFloat(R.styleable.CustomNavigationMapRoute_routeScale, 1.0f);
//
//        // Secondary Routes attributes
//        alternativeRouteDefaultColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_alternativeRouteColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_alternative_color))
//        alternativeRouteModerateColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_alternativeRouteModerateCongestionColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_alternative_congestion_yellow))
//        alternativeRouteSevereColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_alternativeRouteSevereCongestionColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_alternative_congestion_red))
//        alternativeRouteShieldColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_alternativeRouteShieldColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_alternative_shield_color))
//        //        alternativeRouteScale = typedArray.getFloat(
//        //                R.styleable.CustomNavigationMapRoute_alternativeRouteScale, 1.0f);
//
//        // Waypoint attributes
//        originWayPointIcon = typedArray.getResourceId(R.styleable.CustomNavigationMapRoute_originWayPointIcon, R.drawable.ic_route_origin)
//        destinationWayPointIcon = typedArray.getResourceId(R.styleable.CustomNavigationMapRoute_destinationWayPointIcon, R.drawable.ic_route_destination)
//
//        arrowColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_upcomingManeuverArrowColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_upcoming_maneuver_arrow_color))
//        arrowBorderColor = typedArray.getColor(R.styleable.CustomNavigationMapRoute_upcomingManeuverArrowBorderColor,
//                ContextCompat.getColor(context, R.color.mapbox_navigation_route_upcoming_maneuver_arrow_border_color))
//
//        //Route width
//        routeWidth = typedArray.getFloat(R.styleable.CustomNavigationMapRoute_routeLineWidth, 10f)
//        routeShieldLineWidth = typedArray.getFloat(R.styleable.CustomNavigationMapRoute_routeShieldLineWidth, 12f)
//
//        typedArray.recycle()
//    }
//
//
//    /**
//     * Iterate through map style layers backwards till the first not-symbol layer is found.
//     */
//    private fun placeRouteBelow() {
//        if (TextUtils.isEmpty(belowLayer)) {
//            val styleLayers = style?.layers ?: return
//            for (i in styleLayers.indices) {
//                if (styleLayers[i] !is SymbolLayer
//                        // Avoid placing the route on top of the user location layer
//                        && !styleLayers[i].id.contains("mapbox-location")) {
//                    belowLayer = styleLayers[i].id
//                }
//            }
//        }
//    }
//
//    private fun drawWayPointMarkers() {
//        this.originMarker?.let { mapboxMap.removeMarker(it) }
//        this.destinationMarker?.let { mapboxMap.removeMarker(it) }
//        val route = directionsRoutes[primaryRouteIndex]
//        var geometry = ""
//        route.geometry()?.let { geometry = it }
//        val lineString = LineString.fromPolyline(geometry, Constants.PRECISION_5)
//        val coordinates = lineString.coordinates()
//        if (coordinates.size > 0) {
//            this.originMarker = mapboxMap.addMarker(MarkerOptions()
//                    .setIcon(IconFactory.getInstance(mapView.context)
//                            .fromResource(originWayPointIcon))
//                    .position(LatLng(coordinates[0].latitude() / 10
//                            , coordinates[0].longitude() / 10)))
//            this.destinationMarker = mapboxMap.addMarker(MarkerOptions()
//                    .setIcon(IconFactory.getInstance(mapView.context)
//                            .fromResource(destinationWayPointIcon))
//                    .position(LatLng(coordinates[coordinates.size - 1].latitude() / 10
//                            , coordinates[coordinates.size - 1].longitude() / 10)))
//        }
////        val layer = mapboxMap.getLayerAs<SymbolLayer>(WAYPOINT_LAYER_ID)
////        if (layer == null) {
////            var bitmap = MapImageUtils.getBitmapFromDrawable(originMarker)
////            mapboxMap.addImage("originMarker", bitmap)
////            bitmap = MapImageUtils.getBitmapFromDrawable(destinationMarker)
////            mapboxMap.addImage("destinationMarker", bitmap)
////
////            val newLayer = SymbolLayer(WAYPOINT_LAYER_ID, WAYPOINT_SOURCE_ID).withProperties(
////                    PropertyFactory.iconImage(match(
////                            Expression.toString(get(WAY_POINT_KEY)), literal("originMarker"),
////                            stop("origin", literal("originMarker")),
////                            stop("destination", literal("destinationMarker")))),
////                    PropertyFactory.iconSize(interpolate(
////                            exponential(1.5f), zoom(),
////                            stop(0f, 0.6f),
////                            stop(10f, 0.8f),
////                            stop(12f, 1.3f),
////                            stop(22f, 2.8f)
////                    )),
////                    PropertyFactory.iconPitchAlignment(Property.ICON_PITCH_ALIGNMENT_MAP),
////                    PropertyFactory.iconAllowOverlap(true),
////                    PropertyFactory.iconIgnorePlacement(true)
////            )
////            layerIds.add(WAYPOINT_LAYER_ID)
////            MapUtils.addLayerToMap(mapboxMap, newLayer, belowLayer)
////        }
//    }
//
//    private fun getPointFromLineString(leg: RouteLeg, index: Int): Feature? {
//        val steps = leg.steps() ?: return null
//        val feature = Feature.fromGeometry(Point.fromLngLat(
//                steps[index].maneuver().location().longitude(),
//                steps[index].maneuver().location().latitude()
//        ))
//        feature.addStringProperty(SOURCE_KEY, WAYPOINT_SOURCE_ID)
//        feature.addStringProperty(WAY_POINT_KEY, if (index == 0) "origin" else "destination")
//        return feature
//    }
//
//    private fun initialize() {
//        alternativesVisible = true
//        getAttributes()
//        placeRouteBelow()
//        initializeUpcomingManeuverArrow()
//        initializeListeners()
//    }
//
//    private fun initializeListeners() {
//        mapClickListener = MapboxMap.OnMapClickListener { point ->
//            if (invalidMapClick()) {
//                return@OnMapClickListener true
//            }
//            val currentRouteIndex = primaryRouteIndex
//
//            if (findClickedRoute(point)) {
//                return@OnMapClickListener true
//            }
//            checkNewRouteFound(currentRouteIndex)
//            return@OnMapClickListener true
//        }
//        didFinishLoadingStyleListener = MapView.OnDidFinishLoadingStyleListener {
//            placeRouteBelow()
//            initializeUpcomingManeuverArrow()
//            drawRoutes()
//            addDirectionWayPoints()
//            showAlternativeRoutes(alternativesVisible)
//        }
//    }
//
//    private fun addListeners() {
//        if (!isMapClickListenerAdded) {
//            mapboxMap.addOnMapClickListener(mapClickListener)
//            isMapClickListenerAdded = true
//        }
//        navigation?.addProgressChangeListener(progressChangeListener)
//        if (!isDidFinishLoadingStyleListenerAdded) {
//            mapView.addOnDidFinishLoadingStyleListener(didFinishLoadingStyleListener)
//            isDidFinishLoadingStyleListenerAdded = true
//        }
//    }
//
//    private fun removeListeners() {
//        if (isMapClickListenerAdded) {
//            mapboxMap.removeOnMapClickListener(mapClickListener)
//            isMapClickListenerAdded = false
//        }
//        navigation?.removeProgressChangeListener(progressChangeListener)
//        if (isDidFinishLoadingStyleListenerAdded) {
//            mapView.removeOnDidFinishLoadingStyleListener(didFinishLoadingStyleListener)
//            isDidFinishLoadingStyleListenerAdded = false
//        }
//    }
//
//    /**
//     * Remove the route line from the map style.
//     *
//     * @since 0.4.0
//     */
//    fun removeRoute() {
//        clearRoutes()
//    }
//
//    private fun invalidMapClick(): Boolean {
//        return routeLineStrings.isEmpty() || !alternativesVisible
//    }
//
//    private fun findClickedRoute(point: LatLng): Boolean {
//        val routeDistancesAwayFromClick = HashMap<Double, DirectionsRoute>()
//
//        val clickPoint = Point.fromLngLat(point.longitude, point.latitude)
//
//        if (calculateClickDistancesFromRoutes(routeDistancesAwayFromClick, clickPoint)) {
//            return true
//        }
//        val distancesAwayFromClick = ArrayList(routeDistancesAwayFromClick.keys)
//        distancesAwayFromClick.sort()
//
//        val clickedRoute = routeDistancesAwayFromClick[distancesAwayFromClick[0]]
//        primaryRouteIndex = directionsRoutes.indexOf(clickedRoute)
//        return false
//    }
//
//    private fun calculateClickDistancesFromRoutes(routeDistancesAwayFromClick: HashMap<Double, DirectionsRoute>,
//                                                  clickPoint: Point): Boolean {
//        for (lineString in routeLineStrings.keys) {
//            val pointOnLine = findPointOnLine(clickPoint, lineString)
//
//            val distance = TurfMeasurement.distance(clickPoint, pointOnLine, TurfConstants.UNIT_METERS)
//            routeLineStrings[lineString]?.let { routeDistancesAwayFromClick[distance] = it }
//        }
//        return false
//    }
//
//    private fun findPointOnLine(clickPoint: Point, lineString: LineString): Point {
//        val linePoints = lineString.coordinates()
//        val feature = TurfMisc.nearestPointOnLine(clickPoint, linePoints)
//        return feature.geometry() as Point
//    }
//
//    private fun checkNewRouteFound(currentRouteIndex: Int) {
//        if (currentRouteIndex != primaryRouteIndex) {
//            updateRoute()
//            val isValidPrimaryIndex = primaryRouteIndex >= 0 && primaryRouteIndex < directionsRoutes.size
//            if (isValidPrimaryIndex && onRouteSelectionChangeListener != null) {
//                val selectedRoute = directionsRoutes[primaryRouteIndex]
//                onRouteSelectionChangeListener?.onNewPrimaryRouteSelected(selectedRoute)
//            }
//        }
//    }
//
//    private fun updateRoute() {
//        // Update all route geometries to reflect their appropriate colors depending on if they are
//        // alternative or primary.
//        if (primaryRouteIndex !in 0 until directionsRoutes.size) return
//        val data = ArrayList<DirectionsRoute>()
//        data.add(directionsRoutes[primaryRouteIndex])
//        for (i in 0 until directionsRoutes.size) {
//            if (i != primaryRouteIndex) data.add(directionsRoutes[i])
//        }
//        removeRoute()
//        addRoutes(data)
////        for (featureCollection in featureCollections) {
////            val features = featureCollection.features()
////            if (features != null && features.size > 0) {
////                val geometry = features[0].geometry()
////                if (geometry !is Point) {
////                    val index = features[0].getNumberProperty(INDEX_KEY).toInt()
////                    updatePrimaryShieldRoute(String.format(Locale.US, ID_FORMAT, GENERIC_ROUTE_SHIELD_LAYER_ID, index), index)
////                    updatePrimaryRoute(String.format(Locale.US, ID_FORMAT, GENERIC_ROUTE_LAYER_ID, index), index)
////                }
////            }
////        }
//    }
//
//    /**
//     * This method should be called only if you have passed [MapboxNavigation]
//     * into the constructor.
//     *
//     *
//     * This method will add the [ProgressChangeListener] that was originally added so updates
//     * to the [MapboxMap] continue.
//     *
//     * @since 0.15.0
//     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart() {
//        addListeners()
//    }
//
//    /**
//     * This method should be called only if you have passed [MapboxNavigation]
//     * into the constructor.
//     *
//     *
//     * This method will remove the [ProgressChangeListener] that was originally added so updates
//     * to the [MapboxMap] discontinue.
//     *
//     * @since 0.15.0
//     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop() {
//        removeListeners()
//    }
//
//    /**
//     * If the [DirectionsRoute] request contains congestion information via annotations, breakup
//     * the source into pieces so data-driven styling can be used to change the route colors
//     * accordingly.
//     */
//    private fun addTrafficToSource(route: DirectionsRoute, index: Int): FeatureCollection {
//        val features = ArrayList<Feature>()
//        val geometry = route.geometry()
//        if (geometry != null) {
//            val originalGeometry = LineString.fromPolyline(geometry, Constants.PRECISION_6)
//            buildRouteFeatureFromGeometry(index, features, originalGeometry)
//            routeLineStrings[originalGeometry] = route
//
//            val lineString = LineString.fromPolyline(geometry, Constants.PRECISION_6)
//            buildTrafficFeaturesFromRoute(route, index, features, lineString)
//        }
//        return FeatureCollection.fromFeatures(features)
//    }
//
//    private fun buildRouteFeatureFromGeometry(index: Int, features: MutableList<Feature>, originalGeometry: LineString) {
//        val feat = Feature.fromGeometry(originalGeometry)
//        feat.addStringProperty(SOURCE_KEY, String.format(Locale.US, ID_FORMAT, GENERIC_ROUTE_SOURCE_ID, index))
//        feat.addNumberProperty(INDEX_KEY, index)
//        features.add(feat)
//    }
//
//    private fun buildTrafficFeaturesFromRoute(route: DirectionsRoute, index: Int,
//                                              features: MutableList<Feature>, lineString: LineString) {
//        val legs = route.legs() ?: return
//        for (leg in legs) {
//            val congestion = leg.annotation()?.congestion()
//            if (congestion != null) {
//                for (i in 0 until congestion.size) {
//                    // See https://github.com/mapbox/mapbox-navigation-android/issues/353
//                    if (congestion.size + 1 <= lineString.coordinates().size) {
//                        val points = ArrayList<Point>()
//                        points.add(lineString.coordinates()[i])
//                        points.add(lineString.coordinates()[i + 1])
//
//                        val congestionLineString = LineString.fromLngLats(points)
//                        val feature = Feature.fromGeometry(congestionLineString)
//                        feature.addStringProperty(CONGESTION_KEY, congestion[i])
//                        feature.addStringProperty(SOURCE_KEY, String.format(Locale.US, ID_FORMAT,
//                                GENERIC_ROUTE_SOURCE_ID, index))
//                        feature.addNumberProperty(INDEX_KEY, index)
//                        features.add(feature)
//                    }
//                }
//            } else {
//                val feature = Feature.fromGeometry(lineString)
//                features.add(feature)
//            }
//        }
//    }
//
//    private fun updateMapSourceFromFeatureCollection(collection: FeatureCollection?, sourceId: String) {
//        var fc = collection
//        if (fc == null) {
//            fc = FeatureCollection.fromFeatures(arrayOf())
//        }
//        val source = style?.getSourceAs<GeoJsonSource>(sourceId)
//        if (source == null) {
//            val routeGeoJsonOptions = GeoJsonOptions().withMaxZoom(16)
//            val routeSource = GeoJsonSource(sourceId, fc, routeGeoJsonOptions)
//            style?.addSource(routeSource)
//        } else {
//            source.setGeoJson(fc)
//        }
//    }
//
//    private inner class MapRouteProgressChangeListener(private val mapRoute: AppCompatNavigationMapRoute) : ProgressChangeListener {
//        private var isVisible = true
//
//        override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
//            if (!isVisible) {
//                return
//            }
//            val currentRoute = routeProgress.directionsRoute()
//            val directionsRoutes = mapRoute.retrieveDirectionsRoutes()
//            val primaryRouteIndex = mapRoute.retrievePrimaryRouteIndex()
//            addNewRoute(currentRoute, directionsRoutes, primaryRouteIndex)
//            mapRoute.addUpcomingManeuverArrow(routeProgress)
//        }
//
//        fun updateVisibility(isVisible: Boolean) {
//            this.isVisible = isVisible
//        }
//
//        private fun addNewRoute(currentRoute: DirectionsRoute, directionsRoutes: List<DirectionsRoute>,
//                                primaryRouteIndex: Int) {
//            if (isANewRoute(currentRoute, directionsRoutes, primaryRouteIndex)) {
//                mapRoute.addRoute(currentRoute)
//            }
//        }
//
//        private fun isANewRoute(currentRoute: DirectionsRoute, directionsRoutes: List<DirectionsRoute>,
//                                primaryRouteIndex: Int): Boolean {
//            val noRoutes = directionsRoutes.isEmpty()
//            return noRoutes || currentRoute != directionsRoutes[primaryRouteIndex]
//        }
//    }
//}