/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlin-android-extensions")
    id("dev.icerock.mobile.multiplatform")
    id("maven-publish")
}

group = "dev.icerock.moko"
version = Versions.Libs.MultiPlatform.mokoMaps

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.coroutines)

    mppLibrary(Deps.Libs.MultiPlatform.mokoMaps)
    mppLibrary(Deps.Libs.MultiPlatform.ktorClient)
    mppLibrary(Deps.Libs.MultiPlatform.serialization)

    androidLibrary(Deps.Libs.Android.appCompat)
    androidLibrary(Deps.Libs.Android.lifecycle)
    androidLibrary(Deps.Libs.Android.playServicesLocation)
    androidLibrary(Deps.Libs.Android.mapbox)
    androidLibrary(Deps.Libs.Android.mapboxAnnotation)
}

publishing {
    repositories.maven("https://api.bintray.com/maven/icerockdev/moko/moko-maps/;publish=1") {
        name = "bintray"

        credentials {
            username = System.getProperty("BINTRAY_USER")
            password = System.getProperty("BINTRAY_KEY")
        }
    }
}

kotlin {
    targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().forEach { target ->
        target.compilations.getByName("main") {
            val mapBox by cinterops.creating {
                defFile(project.file("src/iosMain/def/MapBox.def"))

                val frameworks = listOf(
                    project.file("../sample/ios-app/Pods/Mapbox-iOS-SDK/dynamic")
                )

                val frameworksOpts = frameworks.map { "-F${it.path}" }
                compilerOpts(*frameworksOpts.toTypedArray())
            }
        }
    }
}
