/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import Foundation
import MapboxMaps
import CoreLocation
import MapKit


//class MapboxAnnotation : MGLPointAnnotation() {
//    var onClick: (() -> Unit)? = null
//    var image: UIImage? = null
//}

//actual class MapboxMarker(
//    private val annotation: MGLPointAnnotation,
//    private val onDeleteCallback: (() -> Unit)?
//) : Marker {
//
//    var onClick: (() -> Unit)? = null
//    var image: UIImage? = null
//
//    override fun delete() {
//        onDeleteCallback?.invoke()
//    }
//
//    override var position: LatLng
//        get() = annotation.coordinate.toLatLng()
//        set(value) {
//            annotation.setCoordinate(coordinate = value.toCoord2D())
//        }
//
//    override var rotation: Float
//        get() = TODO("rotation not work for markers of Mapbox")
//        set(_) { TODO("rotation not work for markers of Mapbox") }
//
//    @OptIn(ExperimentalTime::class)
//    override fun move(position: LatLng, rotation: Float, duration: Duration) {
//        CATransaction.begin()
//        CATransaction.setAnimationDuration(duration.inSeconds)
//
//        annotation.setCoordinate(coordinate = position.toCoord2D())
//
//        CATransaction.commit()
//    }
//
//    fun getAnnotation(): MGLPointAnnotation {
//        return annotation
//    }
//}

@objc
public class MapboxSettingsNative: NSObject {
    public var compassEnabled: Bool = false
    public var myLocationEnabled: Bool = false
    public var scrollGesturesEnabled: Bool = false
    public var zoomGesturesEnabled: Bool = false
    public var tiltGesturesEnabled: Bool = false
    public var rotateGesturesEnabled: Bool = false
    public var logoIsVisible: Bool = false
    public var infoButtonIsVisible: Bool = false
}

@objc
public class MapboxControllerNative: NSObject {
    private let mapView: MapView
    private let locationManager = CLLocationManager()
    
    init(mapView: MapView) {
        self.mapView = mapView
    }
    
    public func getSettings() -> MapboxSettingsNative {
        let settings = MapboxSettingsNative()
        
        settings.compassEnabled = mapView.ornaments.options.compass.visibility != .hidden
        settings.myLocationEnabled = false // mapView?.showsUserLocation ?: false,
        settings.scrollGesturesEnabled = false // mapView?.scrollEnabled ?: false,
        settings.zoomGesturesEnabled = false // mapView?.zoomEnabled ?: false,
        settings.tiltGesturesEnabled = false // mapView?.pitchEnabled ?: false,
        settings.rotateGesturesEnabled = false // mapView?.rotateEnabled ?: false,
        settings.logoIsVisible = !mapView.ornaments.logoView.isHidden
        settings.infoButtonIsVisible = !mapView.ornaments.attributionButton.isHidden
        
        return settings
    }
    
    public func setSettings(_ settings: MapboxSettingsNative) {
        if settings.compassEnabled {
            mapView.ornaments.options.compass.visibility = .adaptive
        } else {
            mapView.ornaments.options.compass.visibility = .hidden
        }
        
        // it.showsUserLocation = settings.myLocationEnabled
        // it.scrollEnabled = settings.scrollGesturesEnabled
        // it.rotateEnabled = settings.rotateGesturesEnabled
        // it.zoomEnabled = settings.zoomGesturesEnabled
        // it.pitchEnabled = settings.tiltGesturesEnabled
        
        mapView.ornaments.logoView.isHidden = !settings.logoIsVisible
        mapView.ornaments.attributionButton.isHidden = !settings.infoButtonIsVisible
    }
}

//actual class MapboxController(
//    mapView: MGLMapView
//) : MapController {
//
//    private val locationManager = CLLocationManager()
//    private val delegate = MapDelegate(this)
//
//    private val weakMapView = WeakReference(mapView)
//
//    private var isMapLoaded: Boolean = false
//
//    private val markers: MutableMap<MGLPointAnnotation, MapboxMarker> = mutableMapOf()
//
//    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)? = null
//
//    init {
//        weakMapView.get()?.delegate = delegate
//    }
//
//    private fun getCurrentLocation(): LatLng {
//        val location: CLLocation = weakMapView.get()?.userLocation?.location
//            ?: locationManager.location
//            ?: throw IllegalStateException("can't get location")
//
//        return location.coordinate.toLatLng()
//    }

//    override suspend fun getCurrentZoom(): Float {
//        return weakMapView.get()?.zoomLevel?.toFloat() ?: 0f
//    }
//
//    override suspend fun setCurrentZoom(zoom: Float) {
//        weakMapView.get()?.zoomLevel = zoom.toDouble()
//    }
//
//    override suspend fun getZoomConfig(): ZoomConfig {
//        return ZoomConfig(
//            min = weakMapView.get()?.minimumZoomLevel?.toFloat(),
//            max = weakMapView.get()?.maximumZoomLevel?.toFloat()
//        )
//    }
//
//    override suspend fun setZoomConfig(config: ZoomConfig) {
//        weakMapView.get()?.minimumZoomLevel = config.min?.toDouble() ?: DEFAULT_MINIMUM_ZOOM
//        weakMapView.get()?.maximumZoomLevel = config.max?.toDouble() ?: DEFAULT_MAXIMUM_ZOOM
//    }
//
//    override suspend fun getMapCenterLatLng(): LatLng {
//        return weakMapView.get()?.camera?.centerCoordinate?.toLatLng() ?: LatLng(
//            latitude = 0.0,
//            longitude = 0.0
//        )
//    }
//
//    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
//        weakMapView.get()?.setCenterCoordinate(
//            centerCoordinate = latLng.toCoord2D(),
//            zoomLevel = zoom.toDouble(),
//            animated = if (isMapLoaded) {
//                animation
//            } else {
//                false
//            }
//        )
//    }
//
//    override fun showMyLocation(zoom: Float) {
//        val location = getCurrentLocation()
//        showLocation(latLng = location, zoom = zoom, animation = true)
//    }
//
//    override suspend fun addMarker(
//        image: ImageResource,
//        latLng: LatLng,
//        rotation: Float,
//        onClick: (() -> Unit)?
//    ): Marker {
//        val annotation = MGLPointAnnotation()
//        annotation.setCoordinate(coordinate = latLng.toCoord2D())
//
//        // TODO: Need implementation for rotation
//        if (rotation != 0.0f) println("WARNING: rotation not work for markers of Mapbox")
//
//        val marker = MapboxMarker(
//            annotation = annotation,
//            onDeleteCallback = {
//                weakMapView.get()?.removeAnnotation(annotation = annotation)
//                markers.remove(annotation)
//            }
//        )
//
//        marker.onClick = onClick
//        marker.image = image.toUIImage()
//        markers.put(annotation, marker)
//
//        weakMapView.get()?.addAnnotation(annotation)
//
//        return marker
//    }
//
//    actual fun setStyleUrl(styleUrl: String) {
//        weakMapView.get()?.styleURL = NSURL(string = styleUrl)
//    }
//
//    override suspend fun drawPolygon(
//        pointList: List<LatLng>,
//        backgroundColor: Color,
//        lineColor: Color,
//        backgroundOpacity: Float,
//        lineWidth: Float,
//        lineOpacity: Float,
//        lineType: LineType
//    ): MapElement {
//        val polygon: MGLPolygonFeature = memScoped {
//
//            val items = createValues<CLLocationCoordinate2D>(pointList.count()) { pos ->
//                this.longitude = pointList[pos].longitude
//                this.latitude = pointList[pos].latitude
//            }
//            MGLPolygonFeature.polygonWithCoordinates(
//                coords = items.ptr,
//                count = pointList.count().toULong()
//            )
//        }
//        val settings = MapboxPolygonSettings(
//            fillColor = backgroundColor.toUIColor()
//                .colorWithAlphaComponent(backgroundOpacity.toDouble()),
//            lineColor = lineColor.toUIColor().colorWithAlphaComponent(lineOpacity.toDouble())
//        )
//        delegate.polygonSettings[polygon.hashCode()] = settings
//
//        val source = MGLShapeSource(
//            identifier = "line:${(0..Int.MAX_VALUE).random()}",
//            shape = polygon,
//            options = null
//        )
//        delegate.style?.addSource(source)
//
//        val layer = MGLLineStyleLayer(
//            identifier = "line-layer:${(0..Int.MAX_VALUE).random()}",
//            source = source
//        )
//        if (lineType == LineType.DASHED) {
//            layer.lineDashPattern =
//                NSExpression.expressionForConstantValue(List<Double>(2) { 2.0 })
//        }
//        layer.lineWidth = NSExpression.expressionForConstantValue(lineWidth)
//        layer.lineColor = NSExpression.expressionForConstantValue(
//            lineColor.toUIColor().colorWithAlphaComponent(lineOpacity.toDouble())
//        )
//        delegate.style?.addLayer(layer)
//
//        weakMapView.get()?.addOverlay(polygon)
//        return MapboxPolygon {
//            weakMapView.get()?.removeOverlay(polygon)
//            delegate.style?.removeLayer(layer)
//        }
//    }
//
//    override suspend fun buildRoute(
//        points: List<LatLng>,
//        lineColor: Color,
//        markersImage: ImageResource?
//    ): MapElement {
//        TODO("for now MapboxDirections pod can't be cinteroped, so on iOS this not implemented")
//    }
//
//    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
//        TODO("for now MapboxDirections pod can't be cinteroped, so on iOS this not implemented")
//    }
//
//    override suspend fun getSimilarNearAddresses(
//        text: String?,
//        maxResults: Int,
//        maxRadius: Int
//    ): List<MapAddress> {
//        TODO("for now MapboxDirections pod can't be cinteroped, so on iOS this not implemented")
//    }
//
//    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
//    private class MapDelegate(
//        mapController: MapboxController
//    ) : NSObject(), MGLMapViewDelegateProtocol {
//        private val mapController = WeakReference(mapController)
//
//        var polygonSettings: MutableMap<Int, MapboxPolygonSettings> = mutableMapOf()
//
//        var style: MGLStyle? = null
//
//        override fun mapViewDidFinishLoadingMap(mapView: MGLMapView) {
//            mapController.get()?.isMapLoaded = true
//        }
//
//        override fun mapView(mapView: MGLMapView, didFinishLoadingStyle: MGLStyle) {
//            style = didFinishLoadingStyle
//        }
//
//        override fun mapView(
//            mapView: MGLMapView,
//            regionWillChangeWithReason: MGLCameraChangeReason,
//            animated: Boolean
//        ) {
//            when (regionWillChangeWithReason) {
//                MGLCameraChangeReasonGesturePan,
//                MGLCameraChangeReasonGestureZoomIn,
//                MGLCameraChangeReasonGestureZoomOut,
//                MGLCameraChangeReasonGesturePinch -> mapController.get()?.onStartScrollCallback?.invoke(
//                    true
//                )
//                else -> mapController.get()?.onStartScrollCallback?.invoke(false)
//            }
//        }
//
//        @Suppress("RETURN_TYPE_MISMATCH_ON_OVERRIDE")
//        override fun mapView(
//            mapView: MGLMapView,
//            imageForAnnotation: MGLAnnotationProtocol
//        ): MGLAnnotationImage? {
//            val annotation = imageForAnnotation as MGLPointAnnotation
//            val image = mapController.value?.markers?.get(annotation)?.image
//            return if (image != null) {
//                MGLAnnotationImage.annotationImageWithImage(
//                    image = image,
//                    reuseIdentifier = image.toString()
//                )
//            } else {
//                null
//            }
//        }
//
//        // it also called for polygons (may be useful in feature)
//        @Suppress("RETURN_TYPE_MISMATCH_ON_OVERRIDE")
//        override fun mapView(mapView: MGLMapView, didSelectAnnotation: MGLAnnotationProtocol) {
//            val annotation = (didSelectAnnotation as? MGLPointAnnotation)
//            annotation?.let { mapController.value?.markers?.get(it)?.onClick?.invoke() }
//        }
//
//        override fun mapView(
//            mapView: MGLMapView,
//            fillColorForPolygonAnnotation: MGLPolygon
//        ): UIColor {
//            @Suppress("SwallowedException")
//            return try {
//                val settings = polygonSettings.getValue(fillColorForPolygonAnnotation.hashCode())
//                settings.fillColor
//            } catch (exception: NoSuchElementException) {
//                UIColor.blueColor()
//            }
//        }
//
//        @Suppress("RETURN_TYPE_MISMATCH_ON_OVERRIDE")
//        override fun mapView(
//            mapView: MGLMapView,
//            strokeColorForShapeAnnotation: MGLShape
//        ): UIColor {
//            @Suppress("SwallowedException")
//            return try {
//                val settings = polygonSettings.getValue(strokeColorForShapeAnnotation.hashCode())
//                settings.lineColor
//            } catch (exception: NoSuchElementException) {
//                UIColor.blueColor()
//            }
//        }
//    }
//
//    private companion object {
//        const val DEFAULT_MINIMUM_ZOOM: Double = 0.0
//        const val DEFAULT_MAXIMUM_ZOOM: Double = 22.0
//    }
//}
