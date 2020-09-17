/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

import dev.icerock.moko.geo.LatLng
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface Marker : MapElement {
    var position: LatLng
    var rotation: Float

    @OptIn(ExperimentalTime::class)
    fun move(position: LatLng, rotation: Float = 0.0f, duration: Duration)
}
