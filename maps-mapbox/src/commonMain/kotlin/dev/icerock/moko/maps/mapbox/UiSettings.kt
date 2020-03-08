/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

data class UiSettings(
    val compassEnabled: Boolean = false,
    val myLocationEnabled: Boolean = false,
    val scrollGesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
    val logoIsVisible: Boolean = true,
    val infoButtonIsVisible: Boolean = true
)
