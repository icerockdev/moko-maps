./gradlew -PlibraryPublish :maps:publishToMavenLocal
(cd sample/ios-app && pod install)
./gradlew -PprovidersPublish :maps-google:publishToMavenLocal
./gradlew -PprovidersPublish :maps-mapbox:publishToMavenLocal