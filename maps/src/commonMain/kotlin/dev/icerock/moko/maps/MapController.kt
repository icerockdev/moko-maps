/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.resources.ImageResource

@Suppress("TooManyFunctions")
interface MapController {
    fun showMyLocation(zoom: Float)

    fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean = false
    )

    suspend fun getMapCenterLatLng(): LatLng

    suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float = 0.0f,
        onClick: (() -> Unit)? = null
    ): Marker

    suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource? = null
    ): MapElement

    @Suppress("LongParameterList")
    suspend fun drawPolygon(
        pointList: List<LatLng>,
        backgroundColor: Color,
        lineColor: Color,
        backgroundOpacity: Float = 1.0f,
        lineWidth: Float = 3.0f,
        lineOpacity: Float = 1.0f,
        lineType: LineType = LineType.SOLID
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

    suspend fun getCurrentZoom(): Float
    suspend fun setCurrentZoom(zoom: Float)

    suspend fun getZoomConfig(): ZoomConfig
    suspend fun setZoomConfig(config: ZoomConfig)
}
