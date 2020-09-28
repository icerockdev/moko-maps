/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.maps.MapElement

actual class MapboxRoute(
    val line: MapboxLine,
    val markers: List<MapboxMarker>
) : MapElement {
    override fun delete() {
        line.delete()
        markers.forEach { it.delete() }
    }
}
