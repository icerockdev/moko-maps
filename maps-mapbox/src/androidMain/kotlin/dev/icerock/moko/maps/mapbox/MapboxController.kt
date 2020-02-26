package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.*
import dev.icerock.moko.resources.ImageResource

actual class MapboxController : MapController {
    actual suspend fun readUiSettings(): UiSettings {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    actual fun writeUiSettings(settings: UiSettings) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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

    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

}