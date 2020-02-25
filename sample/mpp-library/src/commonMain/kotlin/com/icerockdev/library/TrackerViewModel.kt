/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.maps.mapbox.MapboxController
import dev.icerock.moko.maps.mapbox.UiSettings
import dev.icerock.moko.mvvm.viewmodel.ViewModel

class TrackerViewModel(
    val locationTracker: LocationTracker,
    val mapsController: MapboxController
) : ViewModel() {

    fun start() {
        mapsController.writeUiSettings(
            UiSettings(
                compassEnabled = false,
                myLocationEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotateGesturesEnabled = false
            )
        )

//        viewModelScope.launch {
//            val config = mapsController.getZoomConfig()
//            println("config: $config")
//
//            mapsController.setZoomConfig(
//                ZoomConfig(
//                    min = 10f,
//                    max = 18f
//                )
//            )
//            mapsController.setCurrentZoom(12f)
//        }
//
//        mapsController.onCameraScrollStateChanged = { scrolling, isUserGesture ->
//            println("camera scroll state: $scrolling")
//            println("scroll by user gesture: $isUserGesture ")
//        }
//
//        viewModelScope.launch {
//            val route = mapsController.buildRoute(
//                points = listOf(
//                    LatLng(
//                        latitude = 55.032200,
//                        longitude = 82.889360
//                    ),
//                    LatLng(
//                        latitude = 55.030853,
//                        longitude = 82.920154
//                    ),
//                    LatLng(
//                        latitude = 55.013109,
//                        longitude = 82.926480
//                    )
//                ),
//                lineColor = Color(0xCCCC00FF),
//                markersImage = MR.images.marker
//            )
//
//            val marker1 = mapsController.addMarker(
//                image = MR.images.marker,
//                latLng = LatLng(
//                    latitude = 55.045853,
//                    longitude = 82.920154
//                ),
//                rotation = 0.0f
//            ) {
//                println("marker 1 pressed!")
//            }
//
//            val marker2 = mapsController.addMarker(
//                image = MR.images.marker,
//                latLng = LatLng(
//                    latitude = 55.040853,
//                    longitude = 82.920154
//                ),
//                rotation = 0.0f
//            ) {
//                println("marker 2 pressed!")
//            }
//        }
    }

    override fun onCleared() {
        super.onCleared()

        locationTracker.stopTracking()
    }
}
