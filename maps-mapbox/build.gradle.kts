/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

dependencies {
    commonMainImplementation(libs.coroutines)

    commonMainApi(projects.maps)

    androidMainImplementation(libs.appCompat)
    androidMainImplementation(libs.lifecycle)
    androidMainImplementation(libs.playServicesLocation)
    androidMainImplementation(libs.mapboxAnnotation)
    androidMainImplementation(libs.mapboxServices)
    androidMainApi(libs.mapbox)
    androidMainApi(libs.mapboxNavigation)
}

cocoaPods {
    precompiledPod(
        scheme = "MapboxCoreMaps"
    ) { podsDir, target ->
        val sdkPath = when (target.konanTarget) {
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64,
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_X64 -> "ios-arm64_x86_64-simulator"
            is org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 -> "ios-arm64"
            else -> throw IllegalArgumentException("invalid target $target")
        }
        listOf(
            "MapboxCoreMaps"
        ).map { File(podsDir, "$it/$it.xcframework/$sdkPath") }
    }
}
