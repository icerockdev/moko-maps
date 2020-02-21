/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import cocoapods.GoogleMaps.GMSAddress
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSGeocoder
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPath
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.animateToCameraPosition
import cocoapods.GoogleMaps.animateToZoom
import cocoapods.GoogleMaps.create
import cocoapods.GoogleMaps.kGMSMaxZoomLevel
import cocoapods.GoogleMaps.kGMSMinZoomLevel
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.graphics.toUIColor
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.maps.Marker
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import io.ktor.client.HttpClient
import io.ktor.client.call.ReceivePipelineException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import kotlinx.cinterop.cValue
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.serialization.json.Json
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKLocalSearch
import platform.MapKit.MKLocalSearchRequest
import platform.MapKit.MKMapItem
import platform.UIKit.UIEdgeInsetsZero
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.native.ref.WeakReference

actual class GoogleMapController(
    private var mapView: GMSMapView?,
    private val geoApiKey: String
) : MapController {
    private val httpClient = HttpClient {}
    private val json = Json.nonstrict

    private val geoCoder = GMSGeocoder()
    private val locationManager = CLLocationManager()
    private val delegate = MapDelegate(this)

    actual var onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)? = null

    init {
        mapView?.delegate = delegate
    }

    // call for clear memory
    fun removeMapView() {
        mapView = null
    }

    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
        val gmsAddress: GMSAddress = suspendCoroutine { continuation ->
            val coords = cValue<CLLocationCoordinate2D> {
                this.latitude = latitude
                this.longitude = longitude
            }
            geoCoder.reverseGeocodeCoordinate(coords) { response, error ->
                if (error != null) {
                    continuation.resumeWithException(error.asThrowable())
                    return@reverseGeocodeCoordinate
                }

                @Suppress("UNCHECKED_CAST")
                val addresses = response?.firstResult()?.lines as? List<GMSAddress>
                val firstAddress = addresses?.firstOrNull()

                if (firstAddress == null) {
                    continuation.resumeWithException(IllegalStateException("empty results"))
                    return@reverseGeocodeCoordinate
                }

                continuation.resume(firstAddress)
            }
        }

        return gmsAddress.toString()
    }

    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        val location = getCurrentLocation()

        return suspendCoroutine { continuation ->
            val request = MKLocalSearchRequest()
            request.naturalLanguageQuery = text

            request.region = MKCoordinateRegionMakeWithDistance(
                centerCoordinate = CLLocationCoordinate2DMake(
                    latitude = location.latitude,
                    longitude = location.longitude
                ),
                latitudinalMeters = maxRadius.toDouble(),
                longitudinalMeters = maxRadius.toDouble()
            )


            val search = MKLocalSearch(request = request)

            search.startWithCompletionHandler(mainContinuation { response, error ->
                if (error != null) {
                    continuation.resumeWithException(Throwable(error.localizedDescription))
                    return@mainContinuation
                }

                var addresses = listOf<MapAddress>()
                val items = response?.mapItems ?: listOf<MKMapItem>()


                for (item in items) {
                    val mkItem = item as? MKMapItem
                    var fullAddress = mkItem?.placemark?.name ?: ""

                    val thoroughfare = mkItem?.placemark?.thoroughfare
                    if (thoroughfare != null && !fullAddress.contains(thoroughfare)) {
                        fullAddress = fullAddress.plus(", $thoroughfare")
                    }

                    val subThoroughfare = mkItem?.placemark?.subThoroughfare
                    if (subThoroughfare != null && !fullAddress.contains(subThoroughfare)) {
                        fullAddress = fullAddress.plus(", $subThoroughfare")
                    }

                    mkItem?.placemark?.coordinate?.useContents {

                        val address = MapAddress(
                            address = fullAddress,
                            city = mkItem.placemark.locality,
                            latLng = LatLng(latitude = this.latitude, longitude = this.longitude),
                            distance = maxRadius.toDouble()
                        )
                        addresses = addresses.plus(address)
                    }

                }
                continuation.resume(addresses.take(3))
            })
        }
    }

    private fun getCurrentLocation(): LatLng {
        val location: CLLocation = mapView?.myLocation
            ?: locationManager.location
            ?: throw IllegalStateException("can't get location")

        return location.coordinate.toLatLng()
    }

    override suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): Marker {
        val marker = GMSMarker.markerWithPosition(position = latLng.toCoord2D()).also {
            it.icon = image.toUIImage()
            it.rotation = rotation.toDouble()
            it.map = mapView
            it.tappable = true
            it.userData = onClick
        }
        return GoogleMarker(marker)
    }

    override suspend fun buildRoute(points: List<LatLng>, lineColor: Color, markersImage: ImageResource?): MapElement {
        val builder = HttpRequestBuilder()
        builder.method = HttpMethod.Get

        val origin = points.first()
        val destination = points.last()

        var waypoints = ""

        if (points.count() > 2) {
            waypoints = "&waypoints="
            for (point in points) {
                if (point != origin && point != destination) {
                    waypoints = waypoints.plus("via:${point.latitude},${point.longitude}")
                }
            }
        }

        builder.url {
            val originStr = "origin=${origin.latitude},${origin.longitude}"
            val destinationStr = "destination=${destination.latitude},${destination.longitude}"
            val key = "key=$geoApiKey"
            takeFrom("https://maps.googleapis.com/maps/api/directions/json?$originStr&$destinationStr&$key$waypoints")
        }

        try {
            val result: String = httpClient.request(builder)
            return buildRoute(result, lineColor, markersImage)
        } catch (pipeline: ReceivePipelineException) {
            throw pipeline.cause
        }
    }

    private fun buildRoute(from: String, lineColor: Color, markersImage: ImageResource?): MapElement {
        val direction = json.parse(GDirection.serializer(), from)

        val route = direction.routes.firstOrNull() ?: throw IllegalArgumentException("routes not found")

        val path = GMSPath.pathFromEncodedPath(route.overview_polyline.points)
        val routeLine = GMSPolyline.polylineWithPath(path)

        routeLine.strokeColor = lineColor.toUIColor()
        routeLine.strokeWidth = 3.0
        routeLine.map = mapView

        val startMarker = route.legs.firstOrNull()?.start_location
            ?.takeIf { markersImage != null }
            ?.let {
                GMSMarker.markerWithPosition(it.coord2D()).apply {
                    icon = markersImage!!.toUIImage()
                    map = mapView
                }
            }

        val endMarker = route.legs.lastOrNull()?.end_location
            ?.takeIf { markersImage != null }
            ?.let {
                GMSMarker.markerWithPosition(it.coord2D()).apply {
                    icon = markersImage!!.toUIImage()
                    map = mapView
                }
            }

        val firstLeg = route.legs.firstOrNull()
        val waypoints = firstLeg?.via_waypoint
            ?.takeIf { markersImage != null }
            ?.let { points ->
                points.map {
                    val step = firstLeg.steps[it.step_index]
                    GMSMarker.markerWithPosition(step.end_location.coord2D()).apply {
                        icon = markersImage!!.toUIImage()
                        map = mapView
                    }
                }
            }

        if (path != null) {
            val position = mapView?.cameraForBounds(
                bounds = GMSCoordinateBounds.create(path = path),
                insets = UIEdgeInsetsZero.readValue()
            )

            if (position != null) {
                mapView?.animateToCameraPosition(position)
            }
        }

        return GoogleRoute(
            routeLine = routeLine,
            startMarker = startMarker,
            endMarker = endMarker,
            wayPointsMarkers = waypoints
        )
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        return mapView?.camera?.target!!.toLatLng()
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        val position = GMSCameraPosition(
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            zoom = zoom
        )
        if (animation) {
            mapView?.animateToCameraPosition(position)
        } else {
            mapView?.setCamera(position)
        }
    }

    override fun showMyLocation(zoom: Float) {
        val location = getCurrentLocation()
        val position = GMSCameraPosition(
            latitude = location.latitude,
            longitude = location.longitude,
            zoom = zoom
        )
        mapView?.animateToCameraPosition(position)
    }

    override suspend fun getCurrentZoom(): Float {
        return mapView?.camera!!.zoom
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        mapView?.animateToZoom(zoom)
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        return ZoomConfig(
            min = mapView?.minZoom,
            max = mapView?.maxZoom
        )
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        mapView?.setMinZoom(
            minZoom = config.min ?: kGMSMinZoomLevel,
            maxZoom = config.max ?: kGMSMaxZoomLevel
        )
    }

    actual suspend fun readUiSettings(): UiSettings {
        val settings = mapView!!.settings
        return UiSettings(
            compassEnabled = settings.compassButton,
            myLocationButtonEnabled = settings.myLocationButton,
            indoorLevelPickerEnabled = settings.indoorPicker,
            scrollGesturesEnabled = settings.scrollGestures,
            zoomGesturesEnabled = settings.zoomGestures,
            tiltGesturesEnabled = settings.tiltGestures,
            rotateGesturesEnabled = settings.rotateGestures,
            scrollGesturesDuringRotateOrZoomEnabled = settings.allowScrollGesturesDuringRotateOrZoom
        )
    }

    actual fun writeUiSettings(settings: UiSettings) {
        with(mapView!!.settings) {
            compassButton = settings.compassEnabled
            myLocationButton = settings.myLocationButtonEnabled
            indoorPicker = settings.indoorLevelPickerEnabled
            scrollGestures = settings.scrollGesturesEnabled
            zoomGestures = settings.zoomGesturesEnabled
            tiltGestures = settings.tiltGesturesEnabled
            rotateGestures = settings.rotateGesturesEnabled
            allowScrollGesturesDuringRotateOrZoom = settings.scrollGesturesDuringRotateOrZoomEnabled
        }
        mapView?.myLocationEnabled = settings.myLocationButtonEnabled || settings.myLocationEnabled
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    private class MapDelegate(
        mapController: GoogleMapController
    ) : NSObject(), GMSMapViewDelegateProtocol {
        private val mapController = WeakReference(mapController)

        override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
            val marker: GMSMarker = didTapMarker

            @Suppress("UNCHECKED_CAST")
            (marker.userData as? (() -> Unit))?.invoke()

            return false // not show any info box
        }

        override fun mapView(mapView: GMSMapView, willMove: Boolean) {
            mapController.get()?.onCameraScrollStateChanged?.invoke(true, willMove)
        }

        override fun mapView(mapView: GMSMapView, idleAtCameraPosition: GMSCameraPosition) {
            mapController.get()?.onCameraScrollStateChanged?.invoke(false, false)
        }
    }
}
