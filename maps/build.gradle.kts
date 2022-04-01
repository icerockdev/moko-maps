/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

dependencies {
    commonMainImplementation(libs.coroutines)

    commonMainApi(libs.mokoResources)
    commonMainApi(libs.mokoGeo)
    commonMainApi(libs.mokoGraphics)
    commonMainApi(libs.mokoParcelize)

    commonMainImplementation(libs.mokoPermissions)
}
