/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.google.GoogleMapController
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TrackerViewModel(
    val locationTracker: LocationTracker,
    val mapsController: GoogleMapController
) : ViewModel() {

    fun start() {
        viewModelScope.launch {
            try {
                locationTracker.startTracking()
            } catch (exc: Throwable) {
                println(exc)
            }

            locationTracker.getLocationsFlow()
                .distinctUntilChanged()
                .collect {
                    println("show location: $it")
                    mapsController.showLocation(
                        latLng = it,
                        zoom = 15.0f,
                        animation = true
                    )
                }
        }

        viewModelScope.launch {
            val route = mapsController.buildRoute(
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

            val marker = mapsController.addMarker(
                image = MR.images.marker,
                latLng = LatLng(
                    latitude = 55.040853,
                    longitude = 82.920154
                ),
                rotation = 0.0f
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        locationTracker.stopTracking()
    }
}
