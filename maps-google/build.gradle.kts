/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("publication-convention")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainImplementation(libs.ktorClient)
    commonMainImplementation(libs.kotlinSerialization)

    commonMainApi(projects.maps)
    commonMainApi(libs.mokoGeo)
    commonMainApi(libs.mokoGraphics)

    "androidMainImplementation"(libs.appCompat)
    "androidMainImplementation"(libs.lifecycle)
    "androidMainApi"(libs.playServicesLocation)
    "androidMainApi"(libs.playServicesMaps)
    "androidMainImplementation"(libs.googleMapsServices)
    "androidMainImplementation"(libs.ktorClientOkHttp)

    "iosMainImplementation"(libs.ktorClientIos)
}

cocoaPods {
    precompiledPod(
        scheme = "GoogleMaps",
        extraModules = listOf("GoogleMapsBase"),
        extraLinkerOpts = listOf(
            "GoogleMapsBase", "GoogleMapsCore", "CoreGraphics", "QuartzCore", "UIKit",
            "ImageIO", "OpenGLES", "CoreData", "CoreText", "SystemConfiguration", "Security",
            "CoreTelephony", "CoreImage"
        ).map { "-framework $it" }
    ) { podsDir ->
        listOf(
            File(podsDir, "GoogleMaps/Base/Frameworks"),
            File(podsDir, "GoogleMaps/Maps/Frameworks")
        )
    }
}
