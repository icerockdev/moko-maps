/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.sources.Source

class MapboxLine(
    override val style: Style,
    val source: Source,
    val layer: Layer
) : MapboxStyleElement() {
    override fun delete() {
        style.removeLayer(layer)
        style.removeSource(source)
    }
}
