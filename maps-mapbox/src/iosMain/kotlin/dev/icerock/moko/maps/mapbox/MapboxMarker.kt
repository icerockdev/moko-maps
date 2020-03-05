/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import cocoapods.Mapbox.MGLMapView
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import platform.QuartzCore.CATransaction
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class MapboxMarker(
    private val annotation: MapboxAnnotation,
    private val mapView: MGLMapView
) : Marker {

    override fun delete() {
        mapView.removeAnnotation(annotation = annotation)
    }

    override var position: LatLng
        get() = annotation.coordinate.toLatLng()
        set(value) {
            annotation.setCoordinate(coordinate = value.toCoord2D())
        }

    // TODO: Need implementation
    override var rotation: Float
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    @ExperimentalTime
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(duration.inSeconds)

        annotation.setCoordinate(coordinate = position.toCoord2D())

        CATransaction.commit()
    }
}