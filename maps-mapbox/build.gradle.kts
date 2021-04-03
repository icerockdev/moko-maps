/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)

    commonMainApi(project(":maps"))

    androidMainImplementation(Deps.Libs.Android.appCompat)
    androidMainImplementation(Deps.Libs.Android.lifecycle)
    androidMainImplementation(Deps.Libs.Android.playServicesLocation)
    androidMainImplementation(Deps.Libs.Android.mapbox)
    androidMainImplementation(Deps.Libs.Android.mapboxAnnotation)
    androidMainImplementation(Deps.Libs.Android.mapboxNavigation)
}

cocoaPods {
    precompiledPod(
        scheme = "Mapbox"
    ) { podsDir ->
        listOf(File(podsDir, "Mapbox-iOS-SDK/dynamic"))
    }
}
