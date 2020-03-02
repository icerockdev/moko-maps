object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    const val kotlin = "1.3.61"
    private const val mokoResources = "0.6.0"

    object Plugins {
        const val mokoResources = Versions.mokoResources
    }

    object Libs {
        object Android {
            const val appCompat = "1.1.0"
            const val material = "1.0.0"
            const val lifecycle = "2.0.0"
            const val playServicesLocation = "16.0.0"
            const val playServicesMaps = "16.1.0"
            const val googleMapsServices = "0.2.11"
            const val mapbox = "8.6.2"
            const val mapboxAnnotation = "0.7.0"
        }

        object MultiPlatform {
            const val serialization = "0.14.0"
            const val coroutines = "1.3.3"
            const val ktorClient = "1.2.6"
            const val mokoGeo = "0.1.1"
            const val mokoMaps = "0.3.0"
            const val mokoParcelize = "0.2.0"
            const val mokoPermissions = "0.3.0"
            const val mokoMvvm = "0.4.0"
            const val mokoResources = Versions.mokoResources
            const val mokoGraphics = "0.2.0"
        }
    }
}