/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import platform.CoreLocation.CLLocationCoordinate2DMake

@Serializable
internal data class GDirection(
    val routes: List<GRoute>
)

@Serializable
internal data class GRoute(
    val legs: List<GLeg>,
    @SerialName("overview_polyline")
    val overviewPolyline: GPolyline
)

@Serializable
internal data class GLeg(
    @SerialName("start_location")
    val startLocation: GLocation,
    @SerialName("end_location")
    val endLocation: GLocation,
    val steps: List<GStep>,
    @SerialName("via_waypoint")
    val viaWaypoint: List<GWayPoint>?
)

@Serializable
internal data class GStep(
    @SerialName("end_location")
    val endLocation: GLocation
)

@Serializable
internal data class GWayPoint(
    val location: GLocation,
    @SerialName("step_index")
    val stepIndex: Int
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
