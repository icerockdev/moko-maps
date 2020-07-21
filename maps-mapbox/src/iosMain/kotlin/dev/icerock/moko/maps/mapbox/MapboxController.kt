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
import cocoapods.Mapbox.MGLLineStyleLayer
import cocoapods.Mapbox.MGLMapView
import cocoapods.Mapbox.MGLMapViewDelegateProtocol
import cocoapods.Mapbox.MGLOrnamentVisibility
import cocoapods.Mapbox.MGLPolygon
import cocoapods.Mapbox.MGLPolygonFeature
import cocoapods.Mapbox.MGLShape
import cocoapods.Mapbox.MGLShapeSource
import cocoapods.Mapbox.MGLStyle
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.graphics.toUIColor
import dev.icerock.moko.maps.LineType
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.maps.Marker
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import kotlinx.cinterop.createValues
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSExpression
import platform.Foundation.NSURL
import platform.UIKit.UIColor
import platform.UIKit.hidden
import platform.darwin.NSObject
import kotlin.native.ref.WeakReference

actual class MapboxController(
    mapView: MGLMapView
) : MapController {

    private val locationManager = CLLocationManager()
    private val delegate = MapDelegate(this)

    private val defaultMinimumZoom: Double = 0.0
    private val defaultMaximumZoom: Double = 22.0

    private val weakMapView = WeakReference(mapView)

    private var isMapLoaded: Boolean = false

    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)? = null

    init {
        weakMapView.get()?.delegate = delegate
    }

    private fun getCurrentLocation(): LatLng {
        val location: CLLocation = weakMapView.get()?.userLocation?.location
            ?: locationManager.location
            ?: throw IllegalStateException("can't get location")

        return location.coordinate.toLatLng()
    }

    actual suspend fun readUiSettings(): UiSettings {
        val mapView = weakMapView.get()
        return UiSettings(
            compassEnabled = mapView?.compassView?.compassVisibility != MGLOrnamentVisibility.MGLOrnamentVisibilityHidden,
            myLocationEnabled = mapView?.showsUserLocation ?: false,
            scrollGesturesEnabled = mapView?.scrollEnabled ?: false,
            zoomGesturesEnabled = mapView?.zoomEnabled ?: false,
            tiltGesturesEnabled = mapView?.pitchEnabled ?: false,
            rotateGesturesEnabled = mapView?.rotateEnabled ?: false,
            logoIsVisible = mapView?.logoView?.hidden?.not() ?: false,
            infoButtonIsVisible = mapView?.attributionButton?.hidden?.not() ?: false
        )
    }

    actual suspend fun writeUiSettings(settings: UiSettings) {
        weakMapView.get()?.let {
            it.compassView.compassVisibility = if (settings.compassEnabled) {
                MGLOrnamentVisibility.MGLOrnamentVisibilityAdaptive
            } else {
                MGLOrnamentVisibility.MGLOrnamentVisibilityHidden
            }
            it.showsUserLocation = settings.myLocationEnabled
            it.scrollEnabled = settings.scrollGesturesEnabled
            it.rotateEnabled = settings.rotateGesturesEnabled
            it.zoomEnabled = settings.zoomGesturesEnabled
            it.pitchEnabled = settings.tiltGesturesEnabled
            it.logoView.hidden = settings.logoIsVisible.not()
            it.attributionButton.hidden = settings.infoButtonIsVisible.not()
        }
    }

    override suspend fun getCurrentZoom(): Float {
        return weakMapView.get()?.zoomLevel?.toFloat() ?: 0f
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        weakMapView.get()?.zoomLevel = zoom.toDouble()
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        return ZoomConfig(
            min = weakMapView.get()?.minimumZoomLevel?.toFloat(),
            max = weakMapView.get()?.maximumZoomLevel?.toFloat()
        )
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        weakMapView.get()?.minimumZoomLevel = config.min?.toDouble() ?: defaultMinimumZoom
        weakMapView.get()?.maximumZoomLevel = config.max?.toDouble() ?: defaultMaximumZoom
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        return weakMapView.get()?.camera?.centerCoordinate?.toLatLng() ?: LatLng(
            latitude = 0.0,
            longitude = 0.0
        )
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        weakMapView.get()?.setCenterCoordinate(
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
        weakMapView.get()?.addAnnotation(annotation)

        return MapboxMarker(
            annotation = annotation,
            onDeleteCallback = {
                weakMapView.get()?.removeAnnotation(annotation = annotation)
            })
    }

    actual fun setStyleUrl(styleUrl: String) {
        weakMapView.get()?.styleURL = NSURL(string = styleUrl)
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

        val polygon: MGLPolygonFeature = memScoped {

            val items = createValues<CLLocationCoordinate2D>(pointList.count()) { pos ->
                this.longitude = pointList[pos].longitude
                this.latitude = pointList[pos].latitude
            }
            MGLPolygonFeature.polygonWithCoordinates(
                coords = items.ptr,
                count = pointList.count().toULong()
            )
        }
        val settings = MapboxPolygonSettings(
            fillColor = backgroundColor.toUIColor()
                .colorWithAlphaComponent(backgroundOpacity.toDouble()),
            lineColor = lineColor.toUIColor().colorWithAlphaComponent(lineOpacity.toDouble())
        )
        delegate.polygonSettings[polygon.hashCode()] = settings

        val source = MGLShapeSource(identifier = "line", shape = polygon, options = null)
        delegate.style?.addSource(source)

        val layer = MGLLineStyleLayer(identifier = "line-layer", source = source)
        if (lineType == LineType.DASHED) {
            layer.lineDashPattern =
                NSExpression.expressionForConstantValue(List<Double>(2) { 2.0 })
        }
        layer.lineWidth = NSExpression.expressionForConstantValue(lineWidth)
        layer.lineColor = NSExpression.expressionForConstantValue(lineColor.toUIColor().colorWithAlphaComponent(lineOpacity.toDouble()))
        delegate.style?.addLayer(layer)

        weakMapView.get()?.addOverlay(polygon)
        return MapboxPolygon {
            weakMapView.get()?.removeOverlay(polygon)
            delegate.style?.removeLayer(layer)
        }
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

        var polygonSettings: MutableMap<Int, MapboxPolygonSettings> = mutableMapOf()

        var style: MGLStyle? = null

        override fun mapViewDidFinishLoadingMap(mapView: MGLMapView) {
            mapController.get()?.isMapLoaded = true
        }

        override fun mapView(mapView: MGLMapView, didFinishLoadingStyle: MGLStyle) {
            style = didFinishLoadingStyle
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
                MGLCameraChangeReasonGesturePinch -> mapController.get()?.onStartScrollCallback?.invoke(
                    true
                )
                else -> mapController.get()?.onStartScrollCallback?.invoke(false)
            }
        }

        override fun mapView(
            mapView: MGLMapView,
            imageForAnnotation: MGLAnnotationProtocol
        ): MGLAnnotationImage? {
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

        //it also called for polygons (may be useful in feature)
        override fun mapView(mapView: MGLMapView, didSelectAnnotation: MGLAnnotationProtocol) {
            (didSelectAnnotation as? MapboxAnnotation)?.onClick?.invoke()
        }

        override fun mapView(
            mapView: MGLMapView,
            fillColorForPolygonAnnotation: MGLPolygon
        ): UIColor {

            return try {
                val settings = polygonSettings.getValue(fillColorForPolygonAnnotation.hashCode())
                settings.fillColor
            } catch (exception: NoSuchElementException) {
                UIColor.blueColor()
            }
        }

        override fun mapView(
            mapView: MGLMapView,
            strokeColorForShapeAnnotation: MGLShape
        ): UIColor {
            return try {
                val settings = polygonSettings.getValue(strokeColorForShapeAnnotation.hashCode())
                settings.lineColor
            } catch (exception: NoSuchElementException) {
                UIColor.blueColor()
            }
        }

    }
}

