/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.icerockdev.app.databinding.ActivityMapboxBinding
import com.icerockdev.library.MapboxViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.maps.mapbox.MapboxController
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.permissions.PermissionsController

class MapboxActivity : MvvmActivity<ActivityMapboxBinding, MapboxViewModel>() {
    override val layoutId: Int = R.layout.activity_mapbox
    override val viewModelVariableId: Int = BR.viewModel
    override val viewModelClass: Class<MapboxViewModel> = MapboxViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory {
            MapboxViewModel(
                locationTracker = LocationTracker(
                    permissionsController = PermissionsController(
                        applicationContext = applicationContext
                    )
                ),
                mapsController = MapboxController()
            ).apply { start() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(applicationContext, "YOUR-ACCESS-TOKEN") // or in the application class
        super.onCreate(savedInstanceState)
        viewModel.locationTracker.bind(lifecycle, this, supportFragmentManager)

        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                viewModel.mapsController.bind(
                    lifecycle = lifecycle,
                    context = this,
                    mapboxMap = mapboxMap,
                    mapView = binding.map,
                    style = it
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }
}
