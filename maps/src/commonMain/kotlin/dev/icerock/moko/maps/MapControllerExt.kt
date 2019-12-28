/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps

suspend fun MapController.zoomBy(size: Float = 1.0f) {
    val current = getCurrentZoom()
    setCurrentZoom(current + size)
}

suspend fun MapController.zoomIn(size: Float = 1.0f) = zoomBy(size)

suspend fun MapController.zoomOut(size: Float = 1.0f) = zoomBy(-size)
