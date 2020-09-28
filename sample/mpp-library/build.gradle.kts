plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mokoResources)
    plugin(Deps.Plugins.iosFramework)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)

    commonMainApi(Deps.Libs.MultiPlatform.mokoGeo)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvm)
    commonMainApi(Deps.Libs.MultiPlatform.mokoPermissions.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMaps.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMapsGoogle.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMapsMapbox.common)

    androidMainImplementation(Deps.Libs.Android.lifecycle)
    androidMainImplementation(Deps.Libs.Android.playServicesLocation)
    androidMainImplementation(Deps.Libs.Android.mapbox)

    // workaround https://youtrack.jetbrains.com/issue/KT-41821
    commonMainImplementation("io.ktor:ktor-utils:1.4.0")
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
}

multiplatformResources {
    multiplatformResourcesPackage = "com.icerockdev.library"
}

framework {
    export(Deps.Libs.MultiPlatform.mokoPermissions)
    export(Deps.Libs.MultiPlatform.mokoMaps)
    export(Deps.Libs.MultiPlatform.mokoMapsGoogle)
    export(Deps.Libs.MultiPlatform.mokoMapsMapbox)
}

cocoaPods {
    precompiledPod(
        scheme = "GoogleMaps",
        onlyLink = true
    ) { podsDir ->
        listOf(
            File(podsDir, "GoogleMaps/Base/Frameworks"),
            File(podsDir, "GoogleMaps/Maps/Frameworks")
        )
    }
    precompiledPod(
        scheme = "Mapbox",
        onlyLink = true
    ) { podsDir ->
        listOf(File(podsDir, "Mapbox-iOS-SDK/dynamic"))
    }
}
