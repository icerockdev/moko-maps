plugins {
    id("android-app-convention")
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
