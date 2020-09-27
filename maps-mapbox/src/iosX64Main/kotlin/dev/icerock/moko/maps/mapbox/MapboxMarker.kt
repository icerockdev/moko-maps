/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import platform.QuartzCore.CATransaction
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("ForbiddenComment")
actual class MapboxMarker(
    private val annotation: MapboxAnnotation,
    private val onDeleteCallback: (() -> Unit)?
) : Marker {

    override fun delete() {
        onDeleteCallback?.invoke()
    }

    override var position: LatLng
        get() = annotation.coordinate.toLatLng()
        set(value) {
            annotation.setCoordinate(coordinate = value.toCoord2D())
        }

    override var rotation: Float
        get() = TODO("rotation not work for markers of Mapbox")
        set(value) { TODO("rotation not work for markers of Mapbox") }

    @ExperimentalTime
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(duration.inSeconds)

        annotation.setCoordinate(coordinate = position.toCoord2D())

        CATransaction.commit()
    }
}
