/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import kotlinx.serialization.Serializable
import platform.CoreLocation.CLLocationCoordinate2DMake

@Serializable
internal data class GDirection(
    val routes: List<GRoute>
)

@Serializable
internal data class GRoute(
    val legs: List<GLeg>,
    val overview_polyline: GPolyline
)

@Serializable
internal data class GLeg(
    val start_location: GLocation,
    val end_location: GLocation,
    val steps: List<GStep>,
    val via_waypoint: List<GWayPoint>?
)

@Serializable
internal data class GStep(
    val end_location: GLocation
)

@Serializable
internal data class GWayPoint(
    val location: GLocation,
    val step_index: Int
)

@Serializable
internal data class GPolyline(
    val points: String
)

@Serializable
internal data class GLocation(
    val lat: Double,
    val lng: Double
) {
    fun coord2D() = CLLocationCoordinate2DMake(
        latitude = lat,
        longitude = lng
    )
}
