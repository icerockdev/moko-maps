/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import dev.icerock.moko.maps.MapElement

actual class GoogleRoute(
    private val points: List<Marker>,
    private val polyline: Polyline
) : MapElement {
    override fun delete() {
        points.forEach { it.remove() }
        polyline.remove()
    }
}
