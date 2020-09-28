/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import com.mapbox.mapboxsdk.maps.Style
import dev.icerock.moko.maps.MapElement

abstract class MapboxStyleElement : MapElement {
    abstract val style: Style
}
