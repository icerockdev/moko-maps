/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment
import com.icerockdev.app.databinding.ActivityGoogleMapsBinding
import com.icerockdev.library.GoogleMapViewModel
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.maps.google.GoogleMapController
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.permissions.PermissionsController

class GoogleMapsActivity : MvvmActivity<ActivityGoogleMapsBinding, GoogleMapViewModel>() {
    override val layoutId: Int = R.layout.activity_google_maps
    override val viewModelVariableId: Int = BR.viewModel
    override val viewModelClass: Class<GoogleMapViewModel> = GoogleMapViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory {
            GoogleMapViewModel(
                locationTracker = LocationTracker(
                    permissionsController = PermissionsController(
                        applicationContext = applicationContext
                    )
                ),
                googleMapController = GoogleMapController(
                    // TODO: Replace with your API Key from https://developers.google.com/maps/documentation/ios-sdk/
                    geoApiKey = "YOUR-API-KEY"
                )
            ).apply { start() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.locationTracker.bind(lifecycle, this, supportFragmentManager)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            viewModel.googleMapController.bind(
                lifecycle = lifecycle,
                googleMap = it,
                context = this
            )
        }
    }
}