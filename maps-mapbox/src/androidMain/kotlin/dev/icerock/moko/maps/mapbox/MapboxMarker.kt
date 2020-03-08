/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package dev.icerock.moko.maps.mapbox

import android.animation.ValueAnimator
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class MapboxMarker(
    private val symbol: Symbol,
    private val updateHandler: (Symbol) -> Unit,
    private val removeHandler: (Symbol) -> Unit
) : Marker {
    override var position: LatLng
        get() = symbol.latLng.toGeoLatLng()
        set(value) {
            symbol.latLng = value.toMapboxLatLng()
        }

    override var rotation: Float
        get() = symbol.iconRotate
        set(value) {
            symbol.iconRotate = value
        }

    override fun delete() {
        removeHandler.invoke(symbol)
    }

    @ExperimentalTime
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        val currentPosition = symbol.latLng
        val newPosition = position.toMapboxLatLng()

        val latDiff = newPosition.latitude - currentPosition.latitude
        val lngDiff = newPosition.longitude - currentPosition.longitude

        val currentRotation = symbol.iconRotate

        val rotationDiff = rotation - currentRotation

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            symbol.latLng = com.mapbox.mapboxsdk.geometry.LatLng(
                currentPosition.latitude + latDiff * value,
                currentPosition.longitude + lngDiff * value
            )
            symbol.iconRotate = currentRotation + rotationDiff * value
            updateHandler.invoke(symbol)
        }
        animator.duration = duration.inMilliseconds.toLong()
        animator.start()
    }
}