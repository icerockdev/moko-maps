/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.geo.LatLng
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSError

internal fun NSError.asThrowable() = Throwable("[$domain] - $code - $localizedDescription")

internal fun CValue<CLLocationCoordinate2D>.toLatLng(): LatLng = useContents {
    LatLng(
        latitude = latitude,
        longitude = longitude
    )
}

internal fun LatLng.toCoord2D() = CLLocationCoordinate2DMake(
    latitude = latitude,
    longitude = longitude
)
