/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.maps.google.GoogleMapController
import dev.icerock.moko.maps.google.UiSettings
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.launch

@Suppress("MagicNumber")
class GoogleMapViewModel(
    val permissionsController: PermissionsController,
    val googleMapController: GoogleMapController
) : ViewModel() {

    fun start() {
        googleMapController.onCameraScrollStateChanged = { scrolling, isUserGesture ->
            println("camera scroll state: $scrolling")
            println("scroll by user gesture: $isUserGesture ")
        }

        viewModelScope.launch {
            permissionsController.providePermission(Permission.LOCATION)

            googleMapController.writeUiSettings(
                UiSettings(
                    rotateGesturesEnabled = false,
                    myLocationButtonEnabled = true
                )
            )

            val config = googleMapController.getZoomConfig()
            println("config: $config")

            googleMapController.setZoomConfig(
                ZoomConfig(
                    min = 10f,
                    max = 18f
                )
            )
            googleMapController.setCurrentZoom(12f)

            createRoute()
        }
    }

    private suspend fun createRoute() {
        googleMapController.buildRoute(
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

        googleMapController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 55.045853,
                longitude = 82.920154
            ),
            rotation = 0.0f
        ) {
            println("marker 1 pressed!")
        }

        googleMapController.addMarker(
            image = MR.images.marker,
            latLng = LatLng(
                latitude = 55.040853,
                longitude = 82.920154
            ),
            rotation = 0.0f
        ) {
            println("marker 2 pressed!")
        }
    }
}
