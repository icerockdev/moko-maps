/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.androidGradlePlugin)
        classpath(libs.googleServicesGradlePlugin)
        classpath(libs.firebaseGradlePlugin)
        classpath(libs.mokoGradlePlugin)
        classpath(libs.mobileMultiplatformGradlePlugin)
        classpath(libs.kotlinSerializationGradlePlugin)
        classpath(libs.mokoResourcesGeneratorGradlePlugin)
    }
}

apply(plugin = "dev.icerock.moko.gradle.publication.nexus")
val mokoVersion = libs.versions.mokoMapsVersion.get()
allprojects {
    group = "dev.icerock.moko"
    version = mokoVersion
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
