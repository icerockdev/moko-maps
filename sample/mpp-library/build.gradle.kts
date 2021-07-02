plugins {
    id("com.android.library")
    id("android-base-convention")
    id("detekt-convention")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainApi(libs.mokoGeo)
    commonMainApi(libs.mokoMvvm)
    commonMainApi(libs.mokoPermissions)
    commonMainApi(projects.maps)
    commonMainApi(projects.mapsGoogle)
    commonMainApi(projects.mapsMapbox)
    "androidMainImplementation"(libs.lifecycle)
    "androidMainImplementation"(libs.playServicesLocation)
    "androidMainImplementation"(libs.mapbox)
}

libs {
    multiplatformResourcesPackage = "com.icerockdev.library"
}

framework {
    export(libs.mokoPermissions)
    export(libs.mokoMaps)
    export(libs.mokoMapsGoogle)
    export(libs.mokoMapsMapbox)
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
