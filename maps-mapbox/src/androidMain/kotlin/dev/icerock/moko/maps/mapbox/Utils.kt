/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.geo.LatLng

internal fun LatLng.toMapboxLatLng() = com.mapbox.mapboxsdk.geometry.LatLng(
    latitude,
    longitude
)

internal fun com.mapbox.mapboxsdk.geometry.LatLng.toGeoLatLng() = LatLng(
    latitude = latitude,
    longitude = longitude
)
