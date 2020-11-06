![moko-maps](https://user-images.githubusercontent.com/5010169/71351401-27c14d80-25a6-11ea-9183-17821f6d4212.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://api.bintray.com/packages/icerockdev/moko/moko-maps/images/download.svg) ](https://bintray.com/icerockdev/moko/moko-maps/_latestVersion) ![kotlin-version](https://img.shields.io/badge/kotlin-1.4.10-orange)

# Mobile Kotlin maps module
This is a Kotlin Multiplatform library that provides controls of maps to common code.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Versions](#versions)
- [Installation](#installation)
- [Usage](#usage)
- [Samples](#samples)
- [Set Up Locally](#set-up-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Markers** - add markers to map from common code;
- **Route** - draw route by waypoints from common code;
- **Camera** - control camera (zoom, location) from common code.

## Requirements
- Gradle version 6.0+
- Android API 16+
- iOS version 9.0+

## Versions
- kotlin 1.3.61
  - 0.1.0
  - 0.1.1
  - 0.2.0
  - 0.2.1
  - 0.3.0
  - 0.4.0-dev-1
- kotlin 1.3.70
  - 0.4.0-dev-2
  - 0.4.0-dev-3
  - 0.4.0-dev-4
- kotlin 1.4.10
  - 0.5.0
  - 0.5.1

## Installation
root build.gradle  
```groovy
allprojects {
    repositories {
        maven { url = "https://dl.bintray.com/icerockdev/moko" }
        maven { url = "https://mapbox.bintray.com/mapbox" } // if mapbox required
    }
}
```

project build.gradle
```groovy
dependencies {
    commonMainApi("dev.icerock.moko:maps:0.5.1")
    commonMainApi("dev.icerock.moko:maps-google:0.5.1")
    commonMainApi("dev.icerock.moko:maps-mapbox:0.5.1")
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
```

With [mobile-multiplatform-gradle-plugin](https://github.com/icerockdev/mobile-multiplatform-gradle-plugin) cocoapods configuration simplest:
`build.gradle.kts`:
```kotlin
cocoaPods {
    podsProject = file("ios-app/Pods/Pods.xcodeproj")

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
```

project Podfile
```ruby
pod 'GoogleMaps', '3.7.0'
pod 'Mapbox-iOS-SDK', '5.5.0'

# GoogleMaps is static library that already linked in moko-maps-google. Remove duplicated linking.
post_install do |installer|
  host_targets = installer.aggregate_targets.select { |aggregate_target|
    aggregate_target.name.include? "Pods-"
  }
  
  host_targets.each do |host_target|
    host_target.xcconfigs.each do |config_name, config_file|
      config_file.frameworks.delete("GoogleMaps")
      config_file.frameworks.delete("GoogleMapsBase")
      config_file.frameworks.delete("GoogleMapsCore")
      
      xcconfig_path = host_target.xcconfig_path(config_name)
      config_file.save_as(xcconfig_path)
    end
  end
end
```

## Usage
### Markers
```kotlin
class MarkerViewModel(
    val mapsController: GoogleMapController
) : ViewModel() {

    fun start() {
        viewModelScope.launch {
            val marker1 = mapsController.addMarker(
                image = MR.images.marker,
                latLng = LatLng(
                    latitude = 55.045853,
                    longitude = 82.920154
                ),
                rotation = 0.0f
            ) {
                println("marker 1 pressed!")
            }

            marker1.rotation = 90.0f
        }
    }
}
```
### Route
```kotlin
class MarkerViewModel(
    val mapsController: GoogleMapController
) : ViewModel() {

    fun start() {
        viewModelScope.launch {
            val route = mapsController.buildRoute(
                points = listOf(
                    LatLng(
                        latitude = 55.032200,
                        longitude = 82.889360
                    ),
                    LatLng(
                        latitude = 55.030853,
                        longitude = 82.920154
                    ),
                    LatLng(
                        latitude = 55.013109,
                        longitude = 82.926480
                    )
                ),
                lineColor = Color(0xCCCC00FF),
                markersImage = MR.images.marker
            )
        }
    }
}
```

## Samples
Please see more examples in the [sample directory](sample).

## Set Up Locally 
- The [maps directory](maps) contains the base classes for all maps providers;
- The [maps-google directory](maps-google) contains the Google Maps implementation;
- The [maps-mapbox directory](maps-mapbox) contains the mapbox implementation;
- In [sample directory](sample) contains sample apps for Android and iOS; plus the mpp-library connected to the apps.

*before compilation of iOS target required `pod install` in `sample/ios-app` directory*

## Contributing
All development (both new features and bug fixes) is performed in the `develop` branch. This way `master` always contains the sources of the most recently released version. Please send PRs with bug fixes to the `develop` branch. Documentation fixes in the markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` on release.

For more details on contributing please see the [contributing guide](CONTRIBUTING.md).

## License
        
    Copyright 2019 IceRock MAG Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
