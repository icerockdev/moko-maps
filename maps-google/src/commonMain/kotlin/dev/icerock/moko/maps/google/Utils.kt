/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.google

import dev.icerock.moko.graphics.Color

internal fun colorWithOpacity(color: Color, opacity: Float): Color {
    val newAlpha = (color.alpha * opacity).toInt().let {
        if (it > 0xFF) 0xFF
        else it
    }
    return color.copy(alpha = newAlpha)
}
