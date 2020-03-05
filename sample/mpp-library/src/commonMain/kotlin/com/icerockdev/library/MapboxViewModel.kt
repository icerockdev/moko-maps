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
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class MapboxViewModel(
    val locationTracker: LocationTracker,
    val mapsController: MapboxController
) : ViewModel() {

    @UseExperimental(ExperimentalTime::class)
    fun start() {
        viewModelScope.launch {
            locationTracker.startTracking()

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
                    min = null,
                    max = 12f
                )
            )
            mapsController.showMyLocation(8f)
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

            val marker3 = mapsController.addMarker(
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
    }

    override fun onCleared() {
        super.onCleared()

        locationTracker.stopTracking()
    }
}
