/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.LineType
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.maps.mapbox.MapboxController
import dev.icerock.moko.maps.mapbox.UiSettings
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Suppress("MagicNumber")
class MapboxViewModel(
    val permissionsController: PermissionsController,
    val mapsController: MapboxController
) : ViewModel() {

    fun start() {
        mapsController.onStartScrollCallback = { isUserGesture ->
            println("scroll by user gesture: $isUserGesture ")
        }

        viewModelScope.launch {
            permissionsController.providePermission(Permission.LOCATION)

            mapsController.writeUiSettings(
                UiSettings(
                    compassEnabled = false,
                    myLocationEnabled = false,
                    scrollGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                    tiltGesturesEnabled = false,
                    rotateGesturesEnabled = false,
                    infoButtonIsVisible = false,
                    logoIsVisible = false
                )
            )

            val config = mapsController.getZoomConfig()
            println("config: $config")

            mapsController.setZoomConfig(
                ZoomConfig(
                    max = 12f
                )
            )

            @Suppress("TooGenericExceptionCaught")
            try {
                mapsController.showMyLocation(8f)
            } catch (throwable: Throwable) {
                println(throwable.toString())
            }

            @Suppress("TooGenericExceptionCaught")
            try {
                val addresses = mapsController.getSimilarNearAddresses(
                    text = "прибрежная",
                    maxResults = 3,
                    maxRadius = 500
                )
                println(addresses.toString())
            } catch (throwable: Throwable) {
                println(throwable.toString())
            }

            createRoute()
            createMarkers()
            createArea()
        }
    }

    private suspend fun createRoute() {
        mapsController.buildRoute(
            points = listOf(
                LatLng(
                    latitude = 55.032200,
                    longitude = 82.889360
                ),
                LatLng(
                    latitude = 55.030853,
                    longitude = 82.920154
                ),
                LatLng(
                    latitude = 55.013109,
                    longitude = 82.926480
                )
            ),
            lineColor = Color(0xCCCC00FF),
            markersImage = MR.images.marker
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun createMarkers() {
        val marker1 = mapsController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 55.045853,
                longitude = 82.920154
            ),
            rotation = 0.0f
        ) {
            println("marker 1 pressed!")
            mapsController.showLocation(
                latLng = LatLng(
                    latitude = 55.940853,
                    longitude = 82.10154
                ),
                zoom = 8.0f,
                animation = true
            )
        }

        val marker2 = mapsController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 55.940853,
                longitude = 82.10154
            ),
            rotation = 0.0f
        ) {
            println("marker 2 pressed!")
            marker1.move(
                position = LatLng(
                    latitude = 56.0,
                    longitude = 83.0
                ),
                duration = 5.seconds
            )
        }

        mapsController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 55.0,
                longitude = 82.0
            ),
            rotation = 0.0f
        ) {
            println("marker 3 pressed!")
            marker2.delete()
        }
    }

    private suspend fun createArea() {
        val polygon = mapsController.drawPolygon(
            pointList = listOf(
                LatLng(54.97584034615845, 82.87296295166017),
                LatLng(54.99169896662348, 82.87038803100587),
                LatLng(54.993077681033846, 82.91330337524415),
                LatLng(54.98273616833678, 82.89613723754884),
                LatLng(54.97584034615845, 82.87296295166017)
            ),
            backgroundOpacity = 0.5f,
            lineColor = Color(0xFF0000FF),
            backgroundColor = Color(0x227799FF),
            lineType = LineType.DASHED
        )

        mapsController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 54.97623442504603,
                longitude = 82.89665222167969
            ),
            rotation = 0.0f
        ) {
            println("marker 4 pressed!")
            polygon.delete()
        }
    }
}
