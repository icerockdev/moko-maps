package dev.icerock.moko.maps.mapbox

import cocoapods.Mapbox.MGLPointAnnotation
import platform.UIKit.UIImage

class MapboxAnnotation : MGLPointAnnotation() {
    var onClick: (() -> Unit)? = null
    var image: UIImage? = null
}