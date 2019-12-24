/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

import dev.icerock.moko.geo.LatLng

data class MapAddress(
    val address: String?,
    val city: String?,
    val latLng: LatLng,
    val distance: Double = 0.0
)
