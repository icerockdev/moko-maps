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

group = "dev.icerock.moko"
version = Deps.mokoMapsVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClient)
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(project(":maps"))
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoGeo)

    androidMainImplementation(Deps.Libs.Android.appCompat)
    androidMainImplementation(Deps.Libs.Android.lifecycle)
    androidMainImplementation(Deps.Libs.Android.playServicesLocation)
    androidMainImplementation(Deps.Libs.Android.mapbox)
    androidMainImplementation(Deps.Libs.Android.mapboxAnnotation)
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

kotlin.targets
    .matching { it is org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget }
    .configureEach {
        val target = this as org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
