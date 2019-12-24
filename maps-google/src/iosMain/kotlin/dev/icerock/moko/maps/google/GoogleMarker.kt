/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import cocoapods.GoogleMaps.GMSMarker
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import platform.QuartzCore.CATransaction

actual class GoogleMarker(
    private val gmsMarker: GMSMarker
) : Marker {
    override fun delete() {
        gmsMarker.map = null
    }

    override fun move(position: LatLng, rotation: Float) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(2.0)
        gmsMarker.position = position.toCoord2D()
        gmsMarker.rotation = rotation.toDouble()
        CATransaction.commit()
    }
}
