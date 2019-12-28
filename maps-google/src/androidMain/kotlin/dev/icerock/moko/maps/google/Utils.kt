/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import dev.icerock.moko.geo.LatLng

internal fun LatLng.toAndroidLatLng() = com.google.android.gms.maps.model.LatLng(
    latitude,
    longitude
)

internal fun LatLng.toMapsLatLng() = com.google.maps.model.LatLng(
    latitude,
    longitude
)

internal fun com.google.maps.model.LatLng.toAndroidLatLng() = com.google.android.gms.maps.model.LatLng(
    lat,
    lng
)

internal fun com.google.maps.model.LatLng.toGeoLatLng() = LatLng(
    latitude = lat,
    longitude = lng
)

internal fun com.google.android.gms.maps.model.LatLng.toGeoLatLng() = LatLng(
    latitude = latitude,
    longitude = longitude
)
