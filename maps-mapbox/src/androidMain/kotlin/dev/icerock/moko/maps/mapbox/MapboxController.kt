/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.DirectionsCriteria.GEOMETRY_POLYLINE
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnCameraMoveStartedListener.REASON_API_GESTURE
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.graphics.colorInt
import dev.icerock.moko.maps.LineType
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
@Suppress("TooManyFunctions")
actual class MapboxController(
    private val accessToken: String
) : MapController {
    private val contextHolder = LifecycleHolder<Context>()
    private val mapHolder = LifecycleHolder<MapboxMap>()
    private val mapViewHolder = LifecycleHolder<MapView>()
    private val styleHolder = LifecycleHolder<Style>()
    private val geoCoderHolder = LifecycleHolder<Geocoder>()
    private val locationHolder = LifecycleHolder<FusedLocationProviderClient>()

    private lateinit var symbolManager: SymbolManager
    private val symbolActionMap: MutableMap<Long, (() -> Unit)?> = mutableMapOf()

    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)? = null

    fun bind(
        lifecycle: Lifecycle,
        context: Context,
        mapboxMap: MapboxMap,
        mapView: MapView,
        style: Style
    ) {
        contextHolder.set(context)
        mapHolder.set(mapboxMap)
        mapViewHolder.set(mapView)
        styleHolder.set(style)
        locationHolder.set(LocationServices.getFusedLocationProviderClient(context))
        geoCoderHolder.set(Geocoder(context, Locale.getDefault()))

        symbolManager = SymbolManager(
            mapView,
            mapboxMap,
            style
        )

        lifecycle.addObserver(
            object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    contextHolder.clear()
                    mapHolder.clear()
                    mapViewHolder.clear()
                    styleHolder.clear()
                    locationHolder.clear()
                    geoCoderHolder.clear()
                }
            }
        )

        symbolManager.addClickListener {
            symbolActionMap[it.id]?.invoke()
            false
        }

        mapboxMap.addOnCameraMoveStartedListener { reason ->
            onStartScrollCallback?.invoke(reason == REASON_API_GESTURE)
        }
    }

    actual suspend fun readUiSettings(): UiSettings {
        val mapboxMap = mapHolder.get()
        val settings = mapboxMap.uiSettings

        mapboxMap.locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                contextHolder.get(),
                styleHolder.get()
            ).build()
        )

        return UiSettings(
            compassEnabled = settings.isCompassEnabled,
            myLocationEnabled = mapboxMap.locationComponent.isLocationComponentEnabled,
            scrollGesturesEnabled = settings.isScrollGesturesEnabled,
            zoomGesturesEnabled = settings.isZoomGesturesEnabled,
            tiltGesturesEnabled = settings.isTiltGesturesEnabled,
            rotateGesturesEnabled = settings.isRotateGesturesEnabled,
            logoIsVisible = settings.isLogoEnabled,
            infoButtonIsVisible = settings.isAttributionEnabled
        )
    }

    actual suspend fun writeUiSettings(settings: UiSettings) {
        mapHolder.get().locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                contextHolder.get(),
                styleHolder.get()
            ).build()
        )

        mapHolder.doWith {
            with(it.uiSettings) {
                isCompassEnabled = settings.compassEnabled
                isScrollGesturesEnabled = settings.scrollGesturesEnabled
                isZoomGesturesEnabled = settings.zoomGesturesEnabled
                isTiltGesturesEnabled = settings.tiltGesturesEnabled
                isRotateGesturesEnabled = settings.rotateGesturesEnabled
                isLogoEnabled = settings.logoIsVisible
                isAttributionEnabled = settings.infoButtonIsVisible
            }

            it.locationComponent.isLocationComponentEnabled = settings.myLocationEnabled
        }
    }

    override suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): MapboxMarker {
        val style = styleHolder.get()

        val imageId = image.drawableResId.toString()
        if (style.getImage(imageId) == null) {
            val imageDrawable = BitmapUtils.getDrawableFromRes(
                contextHolder.get(),
                image.drawableResId
            ) ?: throw IllegalArgumentException("Drawable for marker is null - $image")
            style.addImage(imageId, imageDrawable)
        }

        val symbol = symbolManager.create(
            SymbolOptions().apply {
                withLatLng(latLng.toMapboxLatLng())
                withIconImage(imageId)
                withIconRotate(rotation)
            }
        )

        symbolActionMap[symbol.id] = onClick

        return MapboxMarker(
            symbol = symbol,
            updateHandler = {
                symbolManager.update(it)
            },
            removeHandler = {
                symbolActionMap.remove(it.id)
                symbolManager.delete(it)
            }
        )
    }

    override suspend fun drawPolygon(
        pointList: List<LatLng>,
        backgroundColor: Color,
        lineColor: Color,
        backgroundOpacity: Float,
        lineWidth: Float,
        lineOpacity: Float,
        lineType: LineType
    ): MapElement {
        val style = styleHolder.get()

        val id: String = pointList.hashCode().toString()
        val sourceId: String = "source-$id"
        val fillLayerId: String = "fill-polygon-$id"
        val lineLayerId: String = "line-polygon-$id"

        val mapboxPointList: List<Point> = pointList.map {
            Point.fromLngLat(it.longitude, it.latitude)
        }

        style.addSource(
            GeoJsonSource(
                sourceId,
                Polygon.fromLngLats(
                    listOf(
                        mapboxPointList
                    )
                )
            )
        )

        val fillLayer: FillLayer = FillLayer(fillLayerId, sourceId)
            .withProperties(
                fillColor(backgroundColor.colorInt()),
                fillOpacity(backgroundOpacity)
            )

        val lineLayer: LineLayer = LineLayer(lineLayerId, sourceId)
            .withProperties(
                lineWidth(lineWidth),
                lineColor(lineColor.colorInt()),
                lineOpacity(lineOpacity),
                lineCap(LINE_CAP_ROUND),
                lineJoin(LINE_JOIN_ROUND),
                when (lineType) {
                    LineType.SOLID -> null
                    LineType.DASHED -> lineDasharray(arrayOf(2f, 2f))
                }
            )

        style.addLayerBelow(fillLayer, SETTLEMENT_LABEL)
        style.addLayerBelow(lineLayer, SETTLEMENT_LABEL)

        return MapboxPolygon(
            onDelete = {
                style.getLayer(fillLayerId)?.also { style.removeLayer(it) }
                style.getLayer(lineLayerId)?.also { style.removeLayer(it) }
            }
        )
    }

    override suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource?
    ): MapboxRoute {
        val directionsClient = MapboxDirections.builder()
            .origin(points.first().let { Point.fromLngLat(it.longitude, it.latitude) })
            .destination(points.last().let { Point.fromLngLat(it.longitude, it.latitude) })
            .apply {
                points.subList(1, points.size - 1).forEach {
                    addWaypoint(Point.fromLngLat(it.longitude, it.latitude))
                }
            }
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .geometries(GEOMETRY_POLYLINE)
            .alternatives(false)
            .steps(true)
            .accessToken(accessToken)
            .build()

        @Suppress("BlockingMethodInNonBlockingContext")
        val directionsRoute = withContext(Dispatchers.Default) {
            val result = directionsClient.executeCall()
            if (!result.isSuccessful) {
                throw IllegalStateException(result.errorBody()!!.string())
            }
            result.body()!!.routes().first()
        }
        val routePoints = directionsRoute.legs()?.flatMap { routeLeg ->
            routeLeg.steps()?.flatMap { legStep ->
                legStep.intersections()?.map { stepIntersection ->
                    stepIntersection.location()
                }.orEmpty()
            }.orEmpty()
        }.orEmpty()

        return MapboxRoute(
            line = drawRouteLine(LineString.fromLngLats(routePoints), lineColor),
            markers = markersImage?.let { markerResource ->
                points.map { addMarker(image = markerResource, latLng = it) }
            }.orEmpty()
        )
    }

    private suspend fun drawRouteLine(
        geometry: Geometry,
        lineColor: Color,
    ): MapboxLine {
        val style = styleHolder.get()

        val id: String = geometry.hashCode().toString()
        val sourceId = "source-line-$id"
        val lineLayerId = "line-line-$id"

        val feature: Feature = Feature.fromGeometry(geometry)
        val geoJsonSource = GeoJsonSource(sourceId, feature)
        style.addSource(geoJsonSource)

        val lineLayer: LineLayer = LineLayer(lineLayerId, sourceId)
            .withProperties(
                lineWidth(WIDTH_POLYLINE),
                lineColor(lineColor.colorInt())
            )

        style.addLayerBelow(lineLayer, SETTLEMENT_LABEL)

        return MapboxLine(
            style = style,
            source = geoJsonSource,
            layer = lineLayer
        )
    }

    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
        val geoCoder = geoCoderHolder.get()

        val locations = withContext(Dispatchers.Default) {
            @Suppress("BlockingMethodInNonBlockingContext")
            geoCoder.getFromLocation(latitude, longitude, SINGLE_RESULT_ADDRESS)
        }

        return locations.getOrNull(0)
            ?.getAddressLine(0)
    }

    override suspend fun getCurrentZoom(): Float {
        return mapHolder.get().cameraPosition.zoom.toFloat()
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        return mapHolder.get().cameraPosition.target.let {
            LatLng(it.latitude, it.longitude)
        }
    }

    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        if (text.isNullOrEmpty()) return emptyList()

        val geoCoder = geoCoderHolder.get()
        val locationProviderClient = locationHolder.get()

        val lastLocation: Location = suspendCoroutine { continuation ->
            locationProviderClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(it.result!!)
                } else {
                    continuation.resumeWithException(it.exception!!)
                }
            }
        }

        // TODO calculate bounds from radius
        @Suppress("MagicNumber")
        val radiusLatitude = maxRadius * 0.001

        @Suppress("MagicNumber")
        val radiusLongitude = maxRadius * 0.001
        return withContext(Dispatchers.IO) {
            val addresses = geoCoder.getFromLocationName(
                text,
                maxResults,
                lastLocation.latitude - radiusLatitude,
                lastLocation.longitude - radiusLongitude,
                lastLocation.latitude + radiusLatitude,
                lastLocation.longitude + radiusLongitude
            )
            addresses.map { address ->
                @Suppress("MagicNumber")
                val distanceResult = FloatArray(3)
                Location.distanceBetween(
                    lastLocation.latitude,
                    lastLocation.longitude,
                    address.latitude,
                    address.longitude,
                    distanceResult
                )
                MapAddress(
                    address = address.thoroughfare,
                    city = null,
                    latLng = LatLng(latitude = address.latitude, longitude = address.longitude),
                    distance = distanceResult[0].toDouble()
                )
            }
        }
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        val map = mapHolder.get()
        return ZoomConfig(
            min = map.minZoomLevel.toFloat(),
            max = map.maxZoomLevel.toFloat()
        )
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        val update = CameraUpdateFactory.zoomTo(zoom.toDouble())
        mapHolder.get().moveCamera(update)
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        with(mapHolder.get()) {
            config.min?.also { setMinZoomPreference(it.toDouble()) }
            config.max?.also { setMaxZoomPreference(it.toDouble()) }
        }
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        val factory = CameraUpdateFactory.newLatLngZoom(
            latLng.toMapboxLatLng(),
            zoom.toDouble()
        )

        mapHolder.doWith { map ->
            when (animation) {
                true -> map.animateCamera(factory)
                false -> map.moveCamera(factory)
            }
        }
    }

    override fun showMyLocation(zoom: Float) {
        locationHolder.doWith { client ->
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                showLocation(
                    latLng = LatLng(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ),
                    zoom = zoom
                )
            }
        }
    }

    actual fun setStyleUrl(styleUrl: String) {
        mapHolder.doWith {
            it.setStyle(styleUrl)
        }
    }

    class LifecycleHolder<T> {
        private var data: T? = null
        private val actions = mutableListOf<(T) -> Unit>()

        fun set(data: T) {
            this.data = data

            with(actions) {
                forEach { it.invoke(data) }
                clear()
            }
        }

        fun clear() {
            this.data = null
        }

        fun doWith(block: (T) -> Unit) {
            val map = data
            if (map == null) {
                actions.add(block)
                return
            }

            block(map)
        }

        suspend fun get(): T = suspendCoroutine { continuation ->
            doWith { continuation.resume(it) }
        }
    }

    private companion object {
        const val SINGLE_RESULT_ADDRESS = 1
        const val WIDTH_POLYLINE = 3.0f
        const val SETTLEMENT_LABEL = "settlement-label"
    }
}
