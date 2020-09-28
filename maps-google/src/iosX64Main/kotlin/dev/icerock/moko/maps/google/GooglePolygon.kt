/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import cocoapods.GoogleMaps.GMSPolygon
import dev.icerock.moko.maps.MapElement

data class GooglePolygon(val polygon: GMSPolygon) : MapElement {
    override fun delete() {
        polygon.map = null
    }
}
