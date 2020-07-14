/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.maps.MapElement

actual class MapboxPolygon(private val onDelete: () -> Unit) : MapElement {
    override fun delete() {
        onDelete()
    }
}