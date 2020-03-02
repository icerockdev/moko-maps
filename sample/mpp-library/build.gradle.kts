plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id(Deps.Plugins.mokoResources.id)
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

val libs = listOf(
    Deps.Libs.MultiPlatform.mokoGeo,
    Deps.Libs.MultiPlatform.mokoMvvm,
    Deps.Libs.MultiPlatform.mokoPermissions,
    Deps.Libs.MultiPlatform.mokoMaps,
    Deps.Libs.MultiPlatform.mokoMapsMapbox,
    Deps.Libs.MultiPlatform.mokoMapsGoogle
)

setupFramework(
    exports = libs
)

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.coroutines)

    androidLibrary(Deps.Libs.Android.lifecycle)
    androidLibrary(Deps.Libs.Android.playServicesLocation)
    androidLibrary(Deps.Libs.Android.mapbox)

    libs.forEach { mppLibrary(it) }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.icerockdev.library"
}


kotlin {
    targets
        .filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
        .flatMap { it.binaries }
        .filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
        .forEach { framework ->
            framework.isStatic = true

            var frameworks = listOf("Base", "Maps").map { frameworkPath ->
                project.file("../ios-app/Pods/GoogleMaps/$frameworkPath/Frameworks").path.let { "-F$it" }
            }

            frameworks.plus(
                project.file("../sample/ios-app/Pods/Mapbox-iOS-SDK/dynamic").path.let { "-F$it" }
            )

            framework.linkerOpts(frameworks)
        }
}
