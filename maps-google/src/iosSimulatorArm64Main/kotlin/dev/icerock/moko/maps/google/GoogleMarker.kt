/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import cocoapods.GoogleMaps.GMSMarker
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import platform.QuartzCore.CATransaction
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class GoogleMarker(
    private val gmsMarker: GMSMarker
) : Marker {
    override var position: LatLng
        get() = gmsMarker.position.toLatLng()
        set(value) {
            gmsMarker.position = value.toCoord2D()
        }

    override var rotation: Float
        get() = gmsMarker.rotation.toFloat()
        set(value) {
            gmsMarker.rotation = value.toDouble()
        }

    override fun delete() {
        gmsMarker.map = null
    }

    @OptIn(ExperimentalTime::class)
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(duration.inSeconds)

        gmsMarker.position = position.toCoord2D()
        gmsMarker.rotation = rotation.toDouble()

        CATransaction.commit()
    }
}
