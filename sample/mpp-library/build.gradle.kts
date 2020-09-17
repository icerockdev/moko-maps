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

kotlin.targets
    .matching { it is org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget }
    .configureEach {
        val target = this as org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

        target.binaries
            .matching { it is org.jetbrains.kotlin.gradle.plugin.mpp.Framework }
            .configureEach {
                val framework = this as org.jetbrains.kotlin.gradle.plugin.mpp.Framework
                val frameworks = listOf("Base", "Maps").map { frameworkPath ->
                    project.file("../ios-app/Pods/GoogleMaps/$frameworkPath/Frameworks").path.let { "-F$it" }
                }.plus(
                    project.file("../ios-app/Pods/Mapbox-iOS-SDK/dynamic").path.let { "-F$it" }
                )

                framework.linkerOpts(frameworks)
            }
    }
