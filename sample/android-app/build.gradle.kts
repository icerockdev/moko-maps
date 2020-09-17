plugins {
    plugin(Deps.Plugins.androidApplication)
    plugin(Deps.Plugins.kotlinAndroid)
    plugin(Deps.Plugins.kotlinKapt)
}

android {
    compileSdkVersion(Deps.Android.compileSdk)

    buildFeatures.dataBinding = true

    dexOptions {
        javaMaxHeapSize = "2g"
    }

    defaultConfig {
        minSdkVersion(Deps.Android.minSdk)
        targetSdkVersion(Deps.Android.targetSdk)

        applicationId = "dev.icerock.moko.samples.maps"

        versionCode = 1
        versionName = "0.1.0"

        vectorDrawables.useSupportLibrary = true

        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(Deps.Libs.Android.appCompat)
    implementation(Deps.Libs.Android.playServicesLocation)
    implementation(Deps.Libs.Android.playServicesMaps)
    implementation(Deps.Libs.Android.googleMapsServices)
    implementation(Deps.Libs.Android.mapbox)
    implementation(Deps.Libs.Android.mapboxAnnotation)

    implementation(Deps.Libs.Android.multidex)

    implementation(project(":sample:mpp-library"))
}
