/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPolyline
import dev.icerock.moko.maps.MapElement

actual class GoogleRoute(
    val routeLine: GMSPolyline,
    val startMarker: GMSMarker?,
    val endMarker: GMSMarker?,
    val wayPointsMarkers: List<GMSMarker>?
) : MapElement {
    override fun delete() {
        wayPointsMarkers?.forEach { it.map = null }
        routeLine.map = null
        startMarker?.map = null
        endMarker?.map = null
    }
}
