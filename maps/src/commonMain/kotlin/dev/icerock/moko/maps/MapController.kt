/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.resources.ImageResource

interface MapController {
    fun showMyLocation(zoom: Float)

    fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean = false
    )

    fun zoomIn(size: Float = 1.0f)
    fun zoomOut(size: Float = 1.0f)

    suspend fun getMapCenterLatLng(): LatLng

    suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float = 0.0f
    ): Marker

    suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource? = null
    ): MapElement

    suspend fun getAddressByLatLng(
        latitude: Double,
        longitude: Double
    ): String?

    suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress>

    fun enableCurrentGeolocation()

    suspend fun requestCurrentLocation(): LatLng
}
