package dev.icerock.moko.maps.mapbox

import dev.icerock.moko.maps.MapElement

actual class MapboxPolygon(
    private val onDelete: () -> Unit
) : MapElement {
    override fun delete() = onDelete.invoke()
}