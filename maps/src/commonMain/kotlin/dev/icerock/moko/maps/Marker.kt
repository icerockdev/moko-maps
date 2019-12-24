/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

import dev.icerock.moko.geo.LatLng

interface Marker : MapElement {
    fun move(position: LatLng, rotation: Float = 0.0f)
}
