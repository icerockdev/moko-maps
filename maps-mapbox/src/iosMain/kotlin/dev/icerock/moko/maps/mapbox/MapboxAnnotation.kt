/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import cocoapods.Mapbox.MGLPointAnnotation
import platform.UIKit.UIImage

class MapboxAnnotation : MGLPointAnnotation() {
    var onClick: (() -> Unit)? = null
    var image: UIImage? = null
}