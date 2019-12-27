/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.PlacesApi
import com.google.maps.errors.NotFoundException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.resources.ImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.maps.model.LatLng as AndroidLatLng
import dev.icerock.moko.geo.LatLng as GeoLatLng

actual class GoogleMapController(
    geoApiKey: String
) : MapController {
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

    private val mapHolder = LifecycleHolder<GoogleMap>()
    private val geoCoderHolder = LifecycleHolder<Geocoder>()
    private val locationHolder = LifecycleHolder<FusedLocationProviderClient>()
    private val geoApiContext: GeoApiContext = GeoApiContext.Builder()
        .apiKey(geoApiKey)
        .build()

    fun bind(lifecycle: Lifecycle, context: Context, googleMap: GoogleMap) {
        mapHolder.set(googleMap)
        locationHolder.set(LocationServices.getFusedLocationProviderClient(context))
        geoCoderHolder.set(Geocoder(context, Locale.getDefault()))

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                mapHolder.clear()
                locationHolder.clear()
                geoCoderHolder.clear()
            }
        })

        googleMap.uiSettings.apply {
            // unsupported on iOS side at all
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
        }

        googleMap.setOnMarkerClickListener { marker ->
            @Suppress("UNCHECKED_CAST")
            (marker.tag as? (() -> Unit))?.invoke()
            false // not show info box
        }
    }

    private suspend fun FusedLocationProviderClient.getLastLocationSuspended(): Location {
        val task: Task<Location> = lastLocation
        return suspendCoroutine { continuation ->
            task.addOnSuccessListener { location ->
                if (location == null) {
                    continuation.resumeWithException(NotFoundException("location not found"))
                    return@addOnSuccessListener
                }

                continuation.resume(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun showMyLocation(zoom: Float) {
        locationHolder.doWith { client ->
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                showLocation(
                    latLng = GeoLatLng(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ),
                    zoom = zoom
                )
            }
        }
    }

    override fun showLocation(latLng: GeoLatLng, zoom: Float, animation: Boolean) {
        val factory = CameraUpdateFactory.newLatLngZoom(
            AndroidLatLng(latLng.latitude, latLng.longitude),
            zoom
        )

        mapHolder.doWith { map ->
            when (animation) {
                true -> map.animateCamera(factory)
                false -> map.moveCamera(factory)
            }
        }
    }

    override fun zoomIn(size: Float) {
        val update = CameraUpdateFactory.zoomBy(size)
        mapHolder.doWith { it.moveCamera(update) }
    }

    override fun zoomOut(size: Float) {
        val update = CameraUpdateFactory.zoomBy(-size)
        mapHolder.doWith { it.moveCamera(update) }
    }

    @SuppressLint("MissingPermission")
    override fun enableCurrentGeolocation() {
        mapHolder.doWith { it.isMyLocationEnabled = true }
    }

    @SuppressLint("MissingPermission")
    override suspend fun requestCurrentLocation(): GeoLatLng {
        return locationHolder.get().getLastLocationSuspended()
            .let { GeoLatLng(latitude = it.latitude, longitude = it.longitude) }
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

    @SuppressLint("MissingPermission")
    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        if (text.isNullOrEmpty()) return emptyList()

        val lastLocation = locationHolder.get().getLastLocationSuspended()

        return with(Dispatchers.IO) {
            val nearbyRequest = PlacesApi.nearbySearchQuery(
                geoApiContext,
                LatLng(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
            ).keyword(text)
                .radius(maxRadius)

            @Suppress("BlockingMethodInNonBlockingContext")
            val nearbyResponse = nearbyRequest.await()

            nearbyResponse.results.map {
                MapAddress(
                    it.name,
                    it.formattedAddress ?: it.vicinity,
                    GeoLatLng(
                        it.geometry.location.lat,
                        it.geometry.location.lng
                    )
                )
            }.take(maxResults)
        }
    }

    override suspend fun getMapCenterLatLng(): GeoLatLng {
        return mapHolder.get().cameraPosition.target.let {
            GeoLatLng(it.latitude, it.longitude)
        }
    }

    override suspend fun buildRoute(
        points: List<GeoLatLng>,
        lineColor: Color,
        markersImage: ImageResource?
    ): MapElement {
        val map = mapHolder.get()

        val from = points.first()
        val to = points.last()
        val markers = mutableListOf<Marker>()

        val origin = from.toMapsLatLng()
        val destination = to.toMapsLatLng()

        fun addRoutePoint(latLng: GeoLatLng) {
            if (markersImage == null) return

            val markerOptions = MarkerOptions()
                .position(latLng.toAndroidLatLng())
                .icon(BitmapDescriptorFactory.fromResource(markersImage.drawableResId))

            val marker = map.addMarker(markerOptions)
            markers.add(marker)
        }

        val waypoints = mutableListOf<LatLng>()
        if (points.size > 2) {
            for (i in 1 until points.size - 1) {
                waypoints.add(points[i].toMapsLatLng())
                addRoutePoint(points[i])
            }
        }

        val directionsResult: DirectionsResult = getDirection(
            origin = origin,
            destination = destination,
            wayPoints = waypoints
        )

        val path = directionsResult.routes
            .getOrNull(0)
            ?.overviewPolyline
            ?.decodePath()
            ?.toList()
            .orEmpty()

        val lines = PolylineOptions()
        lines.width(WIDTH_POLYLINE)
        lines.color(lineColor.argb.toInt())

        val boundsBuilder = LatLngBounds.builder()

        path.map { it.toAndroidLatLng() }.forEach {
            lines.add(it)
            boundsBuilder.include(it)
        }

        val southwestLatLng = boundsBuilder.build().southwest
        val northeastLatLng = boundsBuilder.build().northeast

        val coefficient =
            (southwestLatLng.latitude - northeastLatLng.latitude) * DIVIDER_BOUND_LATITUDE_PADDING

        val latLngPadding = AndroidLatLng(
            southwestLatLng.latitude + coefficient,
            southwestLatLng.longitude
        )
        boundsBuilder.include(latLngPadding)

        val latLongBounds = boundsBuilder.build()

        path.firstOrNull()?.let {
            addRoutePoint(it.toGeoLatLng())
        }

        path.lastOrNull()?.let {
            addRoutePoint(it.toGeoLatLng())
        }

        val polyline = map.addPolyline(lines)
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLongBounds,
                BOUNDS_PADDING
            )
        )

        return GoogleRoute(
            points = markers,
            polyline = polyline
        )
    }

    @Suppress("RedundantWith")
    private suspend fun getDirection(
        origin: LatLng,
        destination: LatLng,
        wayPoints: MutableList<LatLng>
    ): DirectionsResult = with(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            DirectionsApiRequest(geoApiContext)
                .alternatives(false) // не стоить альтернативные пути
                .origin(origin)
                .destination(destination)
                .waypoints(*wayPoints.toTypedArray())
                .setCallback(object : PendingResult.Callback<DirectionsResult> {
                    override fun onResult(result: DirectionsResult) {
                        continuation.resume(result)
                    }

                    override fun onFailure(e: Throwable) {
                        continuation.resumeWithException(e)
                    }
                })
        }
    }

    override suspend fun addMarker(
        image: ImageResource,
        latLng: dev.icerock.moko.geo.LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): dev.icerock.moko.maps.Marker {
        val markerOptions = MarkerOptions()
            .position(latLng.toAndroidLatLng())
            .icon(BitmapDescriptorFactory.fromResource(image.drawableResId))
            .rotation(rotation)
            .anchor(0.5f, 0.5f)

        val marker = mapHolder.get().addMarker(markerOptions)
        marker.tag = onClick
        return GoogleMarker(marker)
    }

    actual suspend fun readUiSettings(): UiSettings {
        val settings = mapHolder.get().uiSettings
        return UiSettings(
            compassEnabled = settings.isCompassEnabled,
            myLocationButtonEnabled = settings.isMyLocationButtonEnabled,
            indoorLevelPickerEnabled = settings.isIndoorLevelPickerEnabled,
            scrollGesturesEnabled = settings.isScrollGesturesEnabled,
            zoomGesturesEnabled = settings.isZoomGesturesEnabled,
            tiltGesturesEnabled = settings.isTiltGesturesEnabled,
            rotateGesturesEnabled = settings.isRotateGesturesEnabled,
            scrollGesturesDuringRotateOrZoomEnabled = settings.isScrollGesturesEnabledDuringRotateOrZoom
        )
    }

    actual fun writeUiSettings(settings: UiSettings) {
        mapHolder.doWith {
            with(it.uiSettings) {
                isCompassEnabled = settings.compassEnabled
                isMyLocationButtonEnabled = settings.myLocationButtonEnabled
                isIndoorLevelPickerEnabled = settings.indoorLevelPickerEnabled
                isScrollGesturesEnabled = settings.scrollGesturesEnabled
                isZoomControlsEnabled = settings.zoomGesturesEnabled
                isTiltGesturesEnabled = settings.tiltGesturesEnabled
                isRotateGesturesEnabled = settings.rotateGesturesEnabled
                isScrollGesturesEnabledDuringRotateOrZoom = settings.scrollGesturesDuringRotateOrZoomEnabled
            }

            it.isMyLocationEnabled = settings.myLocationButtonEnabled || settings.myLocationEnabled
        }
    }

    private companion object {
        const val SINGLE_RESULT_ADDRESS = 1
        const val BOUNDS_PADDING = 250
        const val DIVIDER_BOUND_LATITUDE_PADDING = 2
        const val WIDTH_POLYLINE = 12.0f
    }
}
