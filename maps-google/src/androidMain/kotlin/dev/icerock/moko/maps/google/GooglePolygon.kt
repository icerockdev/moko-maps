/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import com.google.android.gms.maps.model.Polygon
import dev.icerock.moko.maps.MapElement

data class GooglePolygon(val polygon: Polygon) : MapElement {
    override fun delete() {
        polygon.remove()
    }
}
