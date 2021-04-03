/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinKapt)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.kotlinSerialization)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClient)
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(project(":maps"))
    commonMainApi(Deps.Libs.MultiPlatform.mokoGeo)
    commonMainApi(Deps.Libs.MultiPlatform.mokoGraphics)

    androidMainImplementation(Deps.Libs.Android.appCompat)
    androidMainImplementation(Deps.Libs.Android.lifecycle)
    androidMainImplementation(Deps.Libs.Android.playServicesLocation)
    androidMainImplementation(Deps.Libs.Android.playServicesMaps)
    androidMainImplementation(Deps.Libs.Android.googleMapsServices)
    androidMainImplementation(Deps.Libs.Android.ktorClientOkHttp)

    iosMainImplementation(Deps.Libs.Ios.ktorClientIos)
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
