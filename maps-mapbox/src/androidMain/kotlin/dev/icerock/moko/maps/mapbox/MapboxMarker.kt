package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class MapboxMarker : Marker {
    override var position: LatLng
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var rotation: Float
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ExperimentalTime
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}