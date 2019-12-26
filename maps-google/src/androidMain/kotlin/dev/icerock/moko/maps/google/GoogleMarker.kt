/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import android.animation.ValueAnimator
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.maps.Marker
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class GoogleMarker(
    private val gmsMarker: com.google.android.gms.maps.model.Marker
) : Marker {

    override var position: LatLng
        get() = gmsMarker.position.toGeoLatLng()
        set(value) {
            gmsMarker.position = value.toAndroidLatLng()
        }

    override var rotation: Float
        get() = gmsMarker.rotation
        set(value) {
            gmsMarker.rotation = value
        }

    override fun delete() {
        gmsMarker.remove()
    }

    @UseExperimental(ExperimentalTime::class)
    override fun move(position: LatLng, rotation: Float, duration: Duration) {
        val currentPosition = gmsMarker.position
        val newPosition = position.toAndroidLatLng()

        val latDiff = newPosition.latitude - currentPosition.latitude
        val lngDiff = newPosition.longitude - currentPosition.longitude

        val currentRotation = gmsMarker.rotation

        val rotationDiff = rotation - currentRotation

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            gmsMarker.position = com.google.android.gms.maps.model.LatLng(
                currentPosition.latitude + latDiff * value,
                currentPosition.longitude + lngDiff * value
            )
            gmsMarker.rotation = currentRotation + rotationDiff * value
        }
        animator.duration = duration.inMilliseconds.toLong()
        animator.start()
    }
}
