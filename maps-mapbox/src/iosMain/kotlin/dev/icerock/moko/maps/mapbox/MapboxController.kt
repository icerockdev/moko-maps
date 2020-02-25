package dev.icerock.moko.maps.mapbox

import cocoapods.Mapbox.MGLMapView
import cocoapods.Mapbox.MGLOrnamentVisibility
import cocoapods.Mapbox.allowsTilting
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.*
import dev.icerock.moko.resources.ImageResource
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager

actual class MapboxController(
    private val mapView: MGLMapView
) : MapController {

    private val locationManager = CLLocationManager()

    private fun getCurrentLocation(): LatLng {
        val location: CLLocation = mapView.userLocation?.location
            ?: locationManager.location
            ?: throw IllegalStateException("can't get location")

        return location.coordinate.toLatLng()
    }

    actual suspend fun readUiSettings(): UiSettings {
        with(mapView) {
            return UiSettings(
                compassEnabled = if (compassView.compassVisibility == MGLOrnamentVisibility.MGLOrnamentVisibilityHidden) {
                    false
                } else {
                    true
                },
                myLocationEnabled = showsUserLocation,
                scrollGesturesEnabled = scrollEnabled,
                zoomGesturesEnabled = zoomEnabled,
                tiltGesturesEnabled = allowsTilting,
                rotateGesturesEnabled = rotateEnabled
            )
        }
    }

    actual fun writeUiSettings(settings: UiSettings) {
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
            allowsTilting = settings.tiltGesturesEnabled
        }
    }

    actual var onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): Marker {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource?
    ): MapElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getCurrentZoom(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMyLocation(zoom: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}