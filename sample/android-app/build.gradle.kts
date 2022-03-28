plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.moko.gradle.detekt")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    buildFeatures.dataBinding = true

    defaultConfig {
        applicationId = "dev.icerock.moko.samples.maps"

        versionCode = 1
        versionName = "0.1.0"

        multiDexEnabled = true

//        val googleMapsApiKey: String = (System.getenv("GOOGLE_MAPS_API_KEY") ?: extra["googleMaps.apiKey"] as? String).orEmpty()
//        val mapboxPublicToken: String = (System.getenv("MAPBOX_PUBLIC_TOKEN") ?: extra["mapbox.publicToken"] as? String).orEmpty()

//        manifestPlaceholders["googleMapsApiKey"] = googleMapsApiKey
//        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$googleMapsApiKey\"")
//        buildConfigField("String", "MAPBOX_PUBLIC_TOKEN", "\"$mapboxPublicToken\"")
    }
}

dependencies {
    implementation(libs.appCompat)
    implementation(libs.playServicesLocation)
    implementation(libs.playServicesMaps)
    implementation(libs.googleMapsServices)
    implementation(libs.mapbox)

    implementation(libs.multidex)

    implementation(projects.sample.mppLibrary)
}
