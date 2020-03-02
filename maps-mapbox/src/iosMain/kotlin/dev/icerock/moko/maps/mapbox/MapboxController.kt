/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import cocoapods.Mapbox.MGLAnnotationImage
import cocoapods.Mapbox.MGLAnnotationProtocol
import cocoapods.Mapbox.MGLCameraChangeReason
import cocoapods.Mapbox.MGLCameraChangeReasonGesturePan
import cocoapods.Mapbox.MGLCameraChangeReasonGesturePinch
import cocoapods.Mapbox.MGLCameraChangeReasonGestureZoomIn
import cocoapods.Mapbox.MGLCameraChangeReasonGestureZoomOut
import cocoapods.Mapbox.MGLMapView
import cocoapods.Mapbox.MGLMapViewDelegateProtocol
import cocoapods.Mapbox.MGLOrnamentVisibility
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.maps.Marker
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.hidden
import platform.darwin.NSObject
import kotlin.native.ref.WeakReference

actual class MapboxController(
    private val mapView: MGLMapView
) : MapController {

    private val locationManager = CLLocationManager()
    private val delegate = MapDelegate(this)

    private val defaultMinimumZoom: Double = 0.0
    private val defaultMaximumZoom: Double = 22.0

    private var isMapLoaded: Boolean = false

    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)? = null

    init {
        mapView.delegate = delegate
    }

    private fun getCurrentLocation(): LatLng {
        val location: CLLocation = mapView.userLocation?.location
            ?: locationManager.location
            ?: throw IllegalStateException("can't get location")

        return location.coordinate.toLatLng()
    }

    actual suspend fun readUiSettings(): UiSettings {
        with(mapView) {
            return UiSettings(
                compassEnabled = compassView.compassVisibility != MGLOrnamentVisibility.MGLOrnamentVisibilityHidden,
                myLocationEnabled = showsUserLocation,
                scrollGesturesEnabled = scrollEnabled,
                zoomGesturesEnabled = zoomEnabled,
                tiltGesturesEnabled = pitchEnabled,
                rotateGesturesEnabled = rotateEnabled,
                logoIsVisible = logoView.hidden.not(),
                infoButtonIsVisible = attributionButton.hidden.not()
            )
        }
    }
    actual suspend fun writeUiSettings(settings: UiSettings) {
        with(mapView) {
            compassView.compassVisibility = if (settings.compassEnabled) {
                MGLOrnamentVisibility.MGLOrnamentVisibilityAdaptive
            } else {
                MGLOrnamentVisibility.MGLOrnamentVisibilityHidden
            }
            showsUserLocation = settings.myLocationEnabled
            scrollEnabled = settings.scrollGesturesEnabled
            rotateEnabled = settings.rotateGesturesEnabled
            zoomEnabled = settings.zoomGesturesEnabled
            pitchEnabled = settings.tiltGesturesEnabled
            logoView.hidden = settings.logoIsVisible.not()
            attributionButton.hidden = settings.infoButtonIsVisible.not()
        }
    }

    override suspend fun getCurrentZoom(): Float {
        return mapView.zoomLevel.toFloat()
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        mapView.zoomLevel = zoom.toDouble()
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        return ZoomConfig(
            min = mapView.minimumZoomLevel.toFloat(),
            max = mapView.maximumZoomLevel.toFloat()
        )
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        mapView.minimumZoomLevel = config.min?.toDouble() ?: defaultMinimumZoom
        mapView.maximumZoomLevel = config.max?.toDouble() ?: defaultMaximumZoom
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        return mapView.camera.centerCoordinate.toLatLng()
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        mapView.setCenterCoordinate(
            centerCoordinate = latLng.toCoord2D(),
            zoomLevel = zoom.toDouble(),
            animated = if (isMapLoaded) {
                animation
            } else {
                false
            }
        )
    }

    override fun showMyLocation(zoom: Float) {
        val location = getCurrentLocation()
        showLocation(latLng = location, zoom = zoom, animation = true)
    }

    // TODO: Need implementation for rotation
    override suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): Marker {
        val annotation = MapboxAnnotation()
        annotation.setCoordinate(coordinate = latLng.toCoord2D())
        annotation.onClick = onClick
        annotation.image = image.toUIImage()
        mapView.addAnnotation(annotation)

        return MapboxMarker(annotation = annotation, mapView = mapView)
    }

    actual fun setStyleUrl(styleUrl: String) {
        mapView.styleURL = NSURL(string = styleUrl)
    }

    // TODO: Need implementation
    override suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource?
    ): MapElement {
        TODO()
    }

    // TODO: Need implementation
    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
        TODO()
    }

    // TODO: Need implementation
    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        TODO()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
    private class MapDelegate(
        mapController: MapboxController
    ) : NSObject(), MGLMapViewDelegateProtocol {
        private val mapController = WeakReference(mapController)

        override fun mapViewDidFinishLoadingMap(mapView: MGLMapView) {
            mapController.get()?.isMapLoaded = true
        }

        override fun mapView(
            mapView: MGLMapView,
            regionWillChangeWithReason: MGLCameraChangeReason,
            animated: Boolean
        ) {
            when (regionWillChangeWithReason) {
                MGLCameraChangeReasonGesturePan,
                MGLCameraChangeReasonGestureZoomIn,
                MGLCameraChangeReasonGestureZoomOut,
                MGLCameraChangeReasonGesturePinch -> mapController.get()?.onStartScrollCallback?.invoke(true)
                else -> mapController.get()?.onStartScrollCallback?.invoke(false)
            }
        }

        override fun mapView(mapView: MGLMapView, imageForAnnotation: MGLAnnotationProtocol): MGLAnnotationImage? {
            val annotation = imageForAnnotation as MapboxAnnotation
            val image = annotation.image
            return if (image != null) {
                MGLAnnotationImage.annotationImageWithImage(
                    image = image,
                    reuseIdentifier = image.toString()
                )
            } else {
                null
            }
        }

        override fun mapView(mapView: MGLMapView, didSelectAnnotation: MGLAnnotationProtocol) {
            (didSelectAnnotation as MapboxAnnotation).onClick?.invoke()
        }

    }
}

