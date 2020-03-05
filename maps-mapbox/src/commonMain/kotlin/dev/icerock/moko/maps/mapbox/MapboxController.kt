/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.maps.MapController

expect class MapboxController : MapController {
    suspend fun readUiSettings(): UiSettings

    suspend fun writeUiSettings(settings: UiSettings)

    fun setStyleUrl(styleUrl: String)

    var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)?
}
