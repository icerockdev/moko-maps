/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    repositories {
        jcenter()
        google()

        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    }

    resolutionStrategy.eachPlugin {
        val module = Deps.Plugins.list.firstOrNull { it.id == requested.id.id } ?: return@eachPlugin

        useModule(module.artifact)
    }
}

enableFeaturePreview("GRADLE_METADATA")

val properties = startParameter.projectProperties
// ./gradlew -PlibraryPublish :maps:publishToMavenLocal
val libraryPublish: Boolean = properties.containsKey("libraryPublish")
// ./gradlew -PprovidersPublish :maps-google:publishToMavenLocal
val providersPublish: Boolean = properties.containsKey("providersPublish")

include(":maps")
if (!libraryPublish) {
    include(":maps-google")
    include(":maps-mapbox")

    if (!providersPublish) {
        include(":sample:android-app")
        include(":sample:mpp-library")
    }
}
