/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import dev.icerock.moko.maps.MapController

expect class GoogleMapController : MapController {
    suspend fun readUiSettings(): UiSettings

    fun writeUiSettings(settings: UiSettings)
}

data class UiSettings(
    val compassEnabled: Boolean = false,
    val myLocationEnabled: Boolean = false,
    val myLocationButtonEnabled: Boolean = false,
    val indoorLevelPickerEnabled: Boolean = false,
    val scrollGesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
    val scrollGesturesDuringRotateOrZoomEnabled: Boolean = true
)
