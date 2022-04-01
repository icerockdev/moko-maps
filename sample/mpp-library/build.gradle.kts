import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.mobile.multiplatform.cocoapods")
}

kotlin.targets.withType<KotlinNativeTarget>()
    .matching { it.konanTarget.family == org.jetbrains.kotlin.konan.target.Family.IOS }
    .configureEach {
        compilations.all {
            kotlinOptions.freeCompilerArgs += "-Xoverride-konan-properties=osVersionMin.ios_x64=12.0;osVersionMin.ios_arm64=12.0;osVersionMin.ios_simulator_arm64=14.0"
        }
    }

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainImplementation(libs.mokoResources)
    commonMainApi(libs.mokoGeo)
    commonMainApi(libs.mokoMvvmCore)
    commonMainApi(libs.mokoMvvmLiveData)
    commonMainApi(libs.mokoPermissions)
    commonMainApi(projects.maps)
    commonMainApi(projects.mapsGoogle)
    commonMainApi(projects.mapsMapbox)
    androidMainImplementation(libs.lifecycle)
}

multiplatformResources {
    multiplatformResourcesPackage = "com.icerockdev.library"
}

framework {
    export(libs.mokoPermissions)
    export(projects.maps)
    export(projects.mapsGoogle)
    export(projects.mapsMapbox)
}

cocoaPods {
    precompiledPod(
        scheme = "GoogleMaps",
        onlyLink = true
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
    precompiledPod(
        scheme = "Mapbox",
        onlyLink = true
    ) { podsDir, _ ->
        listOf(File(podsDir, "Mapbox-iOS-SDK/dynamic"))
    }
}
