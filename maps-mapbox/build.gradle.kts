/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("publication-convention")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

dependencies {
    commonMainImplementation(libs.coroutines)

    commonMainApi(projects.maps)

    "androidMainImplementation"(libs.appCompat)
    "androidMainImplementation"(libs.lifecycle)
    "androidMainImplementation"(libs.playServicesLocation)
    "androidMainApi"(libs.mapbox)
    "androidMainApi"(libs.mapboxNavigation)
}

cocoaPods {
    precompiledPod(
        scheme = "Mapbox"
    ) { podsDir ->
        listOf(File(podsDir, "Mapbox-iOS-SDK/dynamic"))
    }
}
