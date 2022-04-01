/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
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

    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.lifecycle)
    androidMainApi(libs.playServicesLocation)
    androidMainApi(libs.playServicesMaps)
    androidMainImplementation(libs.googleMapsServices)

    androidMainImplementation(libs.ktorClientOkHttp)
    iosMainImplementation(libs.ktorClientIos)
}

cocoaPods {
    precompiledPod(
        scheme = "GoogleMaps",
        extraModules = listOf("GoogleMapsBase"),
        extraLinkerOpts = listOf(
            "GoogleMapsBase", "GoogleMapsCore", "CoreGraphics", "QuartzCore", "UIKit",
            "ImageIO", "OpenGLES", "CoreData", "CoreText", "SystemConfiguration", "Security",
            "CoreTelephony", "CoreImage", "Metal"
        ).map { "-framework $it" }
    ) { podsDir, target ->
        val sdkPath = when (target.konanTarget) {
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64,
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_X64 -> "ios-x86_64_arm64-simulator"
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 -> "ios-arm64"
            else -> throw IllegalArgumentException("invalid target $target")
        }
        listOf(
            "GoogleMapsBase",
            "GoogleMapsCore",
            "GoogleMaps"
        ).map { File(podsDir, "GoogleMapsXC/$it.xcframework/$sdkPath") }
    }
}
