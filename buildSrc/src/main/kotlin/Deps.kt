/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */


object Deps {
    private const val kotlinVersion = "1.4.10"

    private const val lifecycleVersion = "2.2.0"
    private const val glideVersion = "4.9.0"
    private const val materialVersion = "1.2.0"
    private const val androidAppCompatVersion = "1.1.0"
    private const val espressoCoreVersion = "3.2.0"
    private const val testRunnerVersion = "1.2.0"
    private const val testExtJunitVersion = "1.1.1"
    private const val playServicesLocationVersion = "16.0.0"
    private const val playServicesMapsVersion = "16.1.0"
    private const val googleMapsServicesVersion = "0.2.11"
    private const val mapboxVersion = "8.6.2"
    private const val mapboxAnnotationVersion = "0.7.0"
    private const val multidexVersion = "2.0.1"

    private const val kotlinxSerializationVersion = "1.0.0-RC"
    const val coroutinesVersion = "1.3.9-native-mt"
    private const val ktorClientVersion = "1.4.0"

    private const val detektVersion = "1.12.0"

    private const val mokoGraphicsVersion = "0.4.0"
    private const val mokoParcelizeVersion = "0.4.0"
    private const val mokoResourcesVersion = "0.13.1"
    private const val mokoMvvmVersion = "0.8.0"
    private const val mokoGeoVersion = "0.3.0"
    private const val mokoPermissionsVersion = "0.6.0"
    const val mokoMapsVersion = "0.5.0"

    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 21
    }

    object Plugins {
        val androidApplication = GradlePlugin(id = "com.android.application")
        val androidLibrary = GradlePlugin(id = "com.android.library")
        val kotlinJvm = GradlePlugin(id = "org.jetbrains.kotlin.jvm")
        val kotlinMultiplatform = GradlePlugin(
            id = "org.jetbrains.kotlin.multiplatform",
            module = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        )
        val kotlinKapt = GradlePlugin(id = "kotlin-kapt")
        val kotlinAndroid = GradlePlugin(id = "kotlin-android")
        val kotlinAndroidExtensions = GradlePlugin(id = "kotlin-android-extensions")
        val kotlinSerialization = GradlePlugin(
            id = "org.jetbrains.kotlin.plugin.serialization",
            module = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
        )
        val mavenPublish = GradlePlugin(id = "org.gradle.maven-publish")

        val mobileMultiplatform = GradlePlugin(id = "dev.icerock.mobile.multiplatform")
        val iosFramework = GradlePlugin(id = "dev.icerock.mobile.multiplatform.ios-framework")

        val mokoResources = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-resources",
            module = "dev.icerock.moko:resources-generator:$mokoResourcesVersion"
        )

        val detekt = GradlePlugin(
            id = "io.gitlab.arturbosch.detekt",
            version = detektVersion
        )
    }

    object Libs {
        object Android {
            const val appCompat =
                "androidx.appcompat:appcompat:$androidAppCompatVersion"
            const val glide =
                "com.github.bumptech.glide:glide:$glideVersion"
            const val lifecycle =
                "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"
            const val ktorClientOkHttp =
                "io.ktor:ktor-client-okhttp:$ktorClientVersion"
            const val material =
                "com.google.android.material:material:$materialVersion"
            const val playServicesLocation =
                "com.google.android.gms:play-services-location:$playServicesLocationVersion"
            const val playServicesMaps =
                "com.google.android.gms:play-services-maps:$playServicesMapsVersion"
            const val googleMapsServices =
                "com.google.maps:google-maps-services:$googleMapsServicesVersion"
            const val mapbox =
                "com.mapbox.mapboxsdk:mapbox-android-sdk:$mapboxVersion"
            const val mapboxAnnotation =
                "com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v8:$mapboxAnnotationVersion"
            const val multidex =
                "androidx.multidex:multidex:$multidexVersion"

            object Tests {
                const val espressoCore =
                    "androidx.test.espresso:espresso-core:$espressoCoreVersion"
                const val kotlinTestJUnit =
                    "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
                const val testCore =
                    "androidx.test:core:1.3.0"
                const val robolectric =
                    "org.robolectric:robolectric:4.3"
                const val testRunner =
                    "androidx.test:runner:$testRunnerVersion"
                const val testRules =
                    "androidx.test:rules:$testRunnerVersion"
                const val testExtJunit =
                    "androidx.test.ext:junit:$testExtJunitVersion"
            }
        }

        object MultiPlatform {
            const val kotlinSerialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion"
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            const val ktorClient =
                "io.ktor:ktor-client-core:$ktorClientVersion"
            const val mokoResources =
                "dev.icerock.moko:resources:$mokoResourcesVersion"
            const val mokoParcelize =
                "dev.icerock.moko:parcelize:$mokoParcelizeVersion"
            const val mokoGraphics =
                "dev.icerock.moko:graphics:$mokoGraphicsVersion"
            const val mokoMvvm =
                "dev.icerock.moko:mvvm:$mokoMvvmVersion"
            const val mokoGeo =
                "dev.icerock.moko:geo:$mokoGeoVersion"
            const val mokoPermissions =
                "dev.icerock.moko:permissions:$mokoPermissionsVersion"
            val mokoMaps = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps:$mokoMapsVersion",
                iosX64 = "dev.icerock.moko:maps-iosx64:$mokoMapsVersion",
                iosArm64 = "dev.icerock.moko:maps-iosarm64:$mokoMapsVersion"
            )
            val mokoMapsGoogle = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps-google:$mokoMapsVersion",
                iosX64 = "dev.icerock.moko:maps-google-iosx64:$mokoMapsVersion",
                iosArm64 = "dev.icerock.moko:maps-google-iosarm64:$mokoMapsVersion"
            )
            val mokoMapsMapbox = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps-mapbox:$mokoMapsVersion",
                iosX64 = "dev.icerock.moko:maps-mapbox-iosx64:$mokoMapsVersion",
                iosArm64 = "dev.icerock.moko:maps-mapbox-iosarm64:$mokoMapsVersion"
            )

            object Tests {
                const val kotlinTest =
                    "org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion"
                const val kotlinTestAnnotations =
                    "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion"
            }
        }

        object Ios {
            const val ktorClientIos =
                "io.ktor:ktor-client-ios:$ktorClientVersion"
        }

        object Detekt {
            const val detektFormatting =
                "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
        }
    }
}
