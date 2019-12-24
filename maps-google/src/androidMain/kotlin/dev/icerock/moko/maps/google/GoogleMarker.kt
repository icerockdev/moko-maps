/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker

actual class GoogleMarker(
    private val gmsMarker: com.google.android.gms.maps.model.Marker
) : Marker {
    override fun delete() {
        gmsMarker.remove()
    }

    override fun move(position: LatLng, rotation: Float) {
        gmsMarker.position = position.toAndroidLatLng()
        gmsMarker.rotation = rotation
    }
}
