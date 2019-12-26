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

            val frameworks = listOf(
                "Base" to listOf("GoogleMapsBase"),
                "Maps" to listOf("GoogleMaps", "GoogleMapsCore")
            ).flatMap { (frameworkPath, names) ->
                val searchPath = project.file("../ios-app/Pods/GoogleMaps/$frameworkPath/Frameworks").path
//                names.flatMap { listOf("-framework", it) }.plus("\"-F$searchPath\"")
                listOf("-F$searchPath")
            }

            framework.linkerOpts(frameworks)
        }
}
