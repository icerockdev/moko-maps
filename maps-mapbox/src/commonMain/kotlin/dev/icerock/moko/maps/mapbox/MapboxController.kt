/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.maps.MapController

expect class MapboxController : MapController {
    suspend fun readUiSettings(): UiSettings

    fun writeUiSettings(settings: UiSettings)

    var onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?
}
