/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.maps.mapbox

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnCameraMoveStartedListener.REASON_API_GESTURE
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.maps.MapAddress
import dev.icerock.moko.maps.MapController
import dev.icerock.moko.maps.MapElement
import dev.icerock.moko.maps.Marker
import dev.icerock.moko.maps.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class MapboxController : MapController {
    private val contextHolder = LifecycleHolder<Context>()
    private val mapHolder = LifecycleHolder<MapboxMap>()
    private val mapViewHolder = LifecycleHolder<MapView>()
    private val styleHolder = LifecycleHolder<Style>()
    private val geoCoderHolder = LifecycleHolder<Geocoder>()
    private val locationHolder = LifecycleHolder<FusedLocationProviderClient>()

    private lateinit var symbolManager: SymbolManager
    private val symbolActionMap: MutableMap<Long, (() -> Unit)?> = mutableMapOf()

    actual var onStartScrollCallback: ((isUserGesture: Boolean) -> Unit)? = null

    fun bind(
        lifecycle: Lifecycle,
        context: Context,
        mapboxMap: MapboxMap,
        mapView: MapView,
        style: Style
    ) {
        contextHolder.set(context)
        mapHolder.set(mapboxMap)
        mapViewHolder.set(mapView)
        styleHolder.set(style)
        locationHolder.set(LocationServices.getFusedLocationProviderClient(context))
        geoCoderHolder.set(Geocoder(context, Locale.getDefault()))

        symbolManager = SymbolManager(
            mapView,
            mapboxMap,
            style
        )

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                contextHolder.clear()
                mapHolder.clear()
                mapViewHolder.clear()
                styleHolder.clear()
                locationHolder.clear()
                geoCoderHolder.clear()
            }
        })

        symbolManager.addClickListener {
            symbolActionMap[it.id]?.invoke()
        }

        mapboxMap.addOnCameraMoveStartedListener { reason ->
            onStartScrollCallback?.invoke(reason == REASON_API_GESTURE)
        }
    }

    actual suspend fun readUiSettings(): UiSettings {
        val mapboxMap = mapHolder.get()
        val settings = mapboxMap.uiSettings

        mapboxMap.locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                contextHolder.get(),
                styleHolder.get()
            ).build()
        )

        return UiSettings(
            compassEnabled = settings.isCompassEnabled,
            myLocationEnabled = mapboxMap.locationComponent.isLocationComponentEnabled,
            scrollGesturesEnabled = settings.isScrollGesturesEnabled,
            zoomGesturesEnabled = settings.isZoomGesturesEnabled,
            tiltGesturesEnabled = settings.isTiltGesturesEnabled,
            rotateGesturesEnabled = settings.isRotateGesturesEnabled,
            logoIsVisible = settings.isLogoEnabled,
            infoButtonIsVisible = settings.isAttributionEnabled
        )
    }

    actual suspend fun writeUiSettings(settings: UiSettings) {
        mapHolder.get().locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                contextHolder.get(),
                styleHolder.get()
            ).build()
        )

        mapHolder.doWith {
            with(it.uiSettings) {
                isCompassEnabled = settings.compassEnabled
                isScrollGesturesEnabled = settings.scrollGesturesEnabled
                isZoomGesturesEnabled = settings.zoomGesturesEnabled
                isTiltGesturesEnabled = settings.tiltGesturesEnabled
                isRotateGesturesEnabled = settings.rotateGesturesEnabled
                isLogoEnabled = settings.logoIsVisible
                isAttributionEnabled = settings.infoButtonIsVisible
            }

            it.locationComponent.isLocationComponentEnabled = settings.myLocationEnabled
        }
    }

    override suspend fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float,
        onClick: (() -> Unit)?
    ): Marker {
        val style = styleHolder.get()

        val imageId = image.drawableResId.toString()
        if (style.getImage(imageId) == null) {
            val imageDrawable = BitmapUtils.getDrawableFromRes(
                contextHolder.get(),
                image.drawableResId
            ) ?: throw Exception("Drawable for marker is null")
            style.addImage(imageId, imageDrawable)
        }

        val symbol = symbolManager.create(SymbolOptions().apply {
            withLatLng(latLng.toMapboxLatLng())
            withIconImage(imageId)
            withIconRotate(rotation)
        })

        symbolActionMap[symbol.id] = onClick

        return MapboxMarker(
            symbol = symbol,
            updateHandler = {
                symbolManager.update(it)
            },
            removeHandler = {
                symbolActionMap.remove(it.id)
                symbolManager.delete(it)
            }
        )
    }

    override suspend fun buildRoute(
        points: List<LatLng>,
        lineColor: Color,
        markersImage: ImageResource?
    ): MapElement {
        TODO("not implemented")
    }

    override suspend fun getAddressByLatLng(latitude: Double, longitude: Double): String? {
        val geoCoder = geoCoderHolder.get()

        val locations = withContext(Dispatchers.Default) {
            @Suppress("BlockingMethodInNonBlockingContext")
            geoCoder.getFromLocation(latitude, longitude, SINGLE_RESULT_ADDRESS)
        }

        return locations.getOrNull(0)
            ?.getAddressLine(0)
    }

    override suspend fun getCurrentZoom(): Float {
        return mapHolder.get().cameraPosition.zoom.toFloat()
    }

    override suspend fun getMapCenterLatLng(): LatLng {
        return mapHolder.get().cameraPosition.target.let {
            LatLng(it.latitude, it.longitude)
        }
    }

    override suspend fun getSimilarNearAddresses(
        text: String?,
        maxResults: Int,
        maxRadius: Int
    ): List<MapAddress> {
        TODO("need get nearest addresses")
    }

    override suspend fun getZoomConfig(): ZoomConfig {
        val map = mapHolder.get()
        return ZoomConfig(
            min = map.minZoomLevel.toFloat(),
            max = map.maxZoomLevel.toFloat()
        )
    }

    override suspend fun setCurrentZoom(zoom: Float) {
        val update = CameraUpdateFactory.zoomTo(zoom.toDouble())
        mapHolder.get().moveCamera(update)
    }

    override suspend fun setZoomConfig(config: ZoomConfig) {
        with(mapHolder.get()) {
            config.min?.also { setMinZoomPreference(it.toDouble()) }
            config.max?.also { setMaxZoomPreference(it.toDouble()) }
        }
    }

    override fun showLocation(latLng: LatLng, zoom: Float, animation: Boolean) {
        val factory = CameraUpdateFactory.newLatLngZoom(
            latLng.toMapboxLatLng(),
            zoom.toDouble()
        )

        mapHolder.doWith { map ->
            when (animation) {
                true -> map.animateCamera(factory)
                false -> map.moveCamera(factory)
            }
        }
    }

    override fun showMyLocation(zoom: Float) {
        locationHolder.doWith { client ->
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                showLocation(
                    latLng = LatLng(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ),
                    zoom = zoom
                )
            }
        }
    }

    actual fun setStyleUrl(styleUrl: String) {
        mapHolder.doWith {
            it.setStyle(styleUrl)
        }
    }

    class LifecycleHolder<T> {
        private var data: T? = null
        private val actions = mutableListOf<(T) -> Unit>()

        fun set(data: T) {
            this.data = data

            with(actions) {
                forEach { it.invoke(data) }
                clear()
            }
        }

        fun clear() {
            this.data = null
        }

        fun doWith(block: (T) -> Unit) {
            val map = data
            if (map == null) {
                actions.add(block)
                return
            }

            block(map)
        }

        suspend fun get(): T = suspendCoroutine { continuation ->
            doWith { continuation.resume(it) }
        }
    }

    private companion object {
        const val SINGLE_RESULT_ADDRESS = 1
        const val BOUNDS_PADDING = 250
        const val DIVIDER_BOUND_LATITUDE_PADDING = 2
        const val WIDTH_POLYLINE = 12.0f
    }
}