object Deps {
    object Plugins {
        val kotlinSerialization = GradlePlugin(
            id = "kotlinx-serialization",
            artifact = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        )
        val mokoResources = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-resources",
            artifact = "dev.icerock.moko:resources-generator:${Versions.Plugins.mokoResources}"
        )

        val list: List<GradlePlugin> = listOf(kotlinSerialization, mokoResources)
    }

    object Libs {
        object Android {
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
            )
            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.Libs.Android.appCompat}"
            )
            val material = AndroidLibrary(
                name = "com.google.android.material:material:${Versions.Libs.Android.material}"
            )
            val lifecycle = AndroidLibrary(
                name = "androidx.lifecycle:lifecycle-extensions:${Versions.Libs.Android.lifecycle}"
            )
            val playServicesLocation = AndroidLibrary(
                name = "com.google.android.gms:play-services-location:${Versions.Libs.Android.playServicesLocation}"
            )
            val playServicesMaps = AndroidLibrary(
                name = "com.google.android.gms:play-services-maps:${Versions.Libs.Android.playServicesMaps}"
            )
            val googleMapsServices = AndroidLibrary(
                name = "com.google.maps:google-maps-services:${Versions.Libs.Android.googleMapsServices}"
            )
            val mapbox = AndroidLibrary(
                name = "com.mapbox.mapboxsdk:mapbox-android-sdk:${Versions.Libs.Android.mapbox}"
            )
            val mapboxAnnotation = AndroidLibrary(
                name = "com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v8:${Versions.Libs.Android.mapboxAnnotation}"
            )
        }

        object MultiPlatform {
            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )
            val coroutines = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.MultiPlatform.coroutines}",
                common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.Libs.MultiPlatform.coroutines}",
                ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.Libs.MultiPlatform.coroutines}"
            )
            val ktorClient = MultiPlatformLibrary(
                android = "io.ktor:ktor-client-android:${Versions.Libs.MultiPlatform.ktorClient}",
                common = "io.ktor:ktor-client-core:${Versions.Libs.MultiPlatform.ktorClient}",
                ios = "io.ktor:ktor-client-ios:${Versions.Libs.MultiPlatform.ktorClient}"
            )
            val serialization = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.Libs.MultiPlatform.serialization}",
                common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.Libs.MultiPlatform.serialization}",
                ios = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.Libs.MultiPlatform.serialization}"
            )
            val mokoGeo = MultiPlatformLibrary(
                common = "dev.icerock.moko:geo:${Versions.Libs.MultiPlatform.mokoGeo}",
                iosX64 = "dev.icerock.moko:geo-iosx64:${Versions.Libs.MultiPlatform.mokoGeo}",
                iosArm64 = "dev.icerock.moko:geo-iosarm64:${Versions.Libs.MultiPlatform.mokoGeo}"
            )
            val mokoMaps = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosX64 = "dev.icerock.moko:maps-iosx64:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosArm64 = "dev.icerock.moko:maps-iosarm64:${Versions.Libs.MultiPlatform.mokoMaps}"
            )
            val mokoMapsGoogle = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps-google:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosX64 = "dev.icerock.moko:maps-google-iosx64:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosArm64 = "dev.icerock.moko:maps-google-iosarm64:${Versions.Libs.MultiPlatform.mokoMaps}"
            )
            val mokoMapsMapbox = MultiPlatformLibrary(
                common = "dev.icerock.moko:maps-mapbox:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosX64 = "dev.icerock.moko:maps-mapbox-iosx64:${Versions.Libs.MultiPlatform.mokoMaps}",
                iosArm64 = "dev.icerock.moko:maps-mapbox-iosarm64:${Versions.Libs.MultiPlatform.mokoMaps}"
            )
            val mokoParcelize = MultiPlatformLibrary(
                common = "dev.icerock.moko:parcelize:${Versions.Libs.MultiPlatform.mokoParcelize}",
                iosX64 = "dev.icerock.moko:parcelize-iosx64:${Versions.Libs.MultiPlatform.mokoParcelize}",
                iosArm64 = "dev.icerock.moko:parcelize-iosarm64:${Versions.Libs.MultiPlatform.mokoParcelize}"
            )
            val mokoPermissions = MultiPlatformLibrary(
                common = "dev.icerock.moko:permissions:${Versions.Libs.MultiPlatform.mokoPermissions}",
                iosX64 = "dev.icerock.moko:permissions-iosx64:${Versions.Libs.MultiPlatform.mokoPermissions}",
                iosArm64 = "dev.icerock.moko:permissions-iosarm64:${Versions.Libs.MultiPlatform.mokoPermissions}"
            )
            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:${Versions.Libs.MultiPlatform.mokoMvvm}"
            )
            val mokoResources = MultiPlatformLibrary(
                common = "dev.icerock.moko:resources:${Versions.Libs.MultiPlatform.mokoResources}",
                iosX64 = "dev.icerock.moko:resources-iosx64:${Versions.Libs.MultiPlatform.mokoResources}",
                iosArm64 = "dev.icerock.moko:resources-iosarm64:${Versions.Libs.MultiPlatform.mokoResources}"
            )
            val mokoGraphics = MultiPlatformLibrary(
                common = "dev.icerock.moko:graphics:${Versions.Libs.MultiPlatform.mokoGraphics}",
                iosX64 = "dev.icerock.moko:graphics-iosx64:${Versions.Libs.MultiPlatform.mokoGraphics}",
                iosArm64 = "dev.icerock.moko:graphics-iosarm64:${Versions.Libs.MultiPlatform.mokoGraphics}"
            )
        }
    }
}

data class GradlePlugin(
    val id: String,
    val artifact: String
)
