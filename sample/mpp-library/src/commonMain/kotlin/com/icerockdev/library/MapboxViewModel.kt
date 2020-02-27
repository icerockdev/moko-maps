/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.maps.mapbox.MapboxController
import dev.icerock.moko.maps.mapbox.UiSettings
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch

class MapboxViewModel(
    val locationTracker: LocationTracker,
    val mapsController: MapboxController
) : ViewModel() {

    fun start() {
        mapsController.writeUiSettings(
            UiSettings(
                compassEnabled = false,
                myLocationEnabled = true,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotateGesturesEnabled = false
            )
        )

        viewModelScope.launch {
            val config = mapsController.getZoomConfig()
            println("config: $config")

            mapsController.setZoomConfig(
                ZoomConfig(
                    min = null,
                    max = 10f
                )
            )
            mapsController.setCurrentZoom(zoom = 3f)
        }

        mapsController.onStartScrollCallback = { isUserGesture ->
            println("scroll by user gesture: $isUserGesture ")
        }

        viewModelScope.launch {
            val marker1 = mapsController.addMarker(
                image = MR.images.marker,
                latLng = LatLng(
                    latitude = 55.045853,
                    longitude = 82.920154
                ),
                rotation = 0.0f
            ) {
                println("marker 1 pressed!")
            }

            val marker2 = mapsController.addMarker(
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

    override fun onCleared() {
        super.onCleared()

        locationTracker.stopTracking()
    }
}
