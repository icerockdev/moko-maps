/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("publication-convention")
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
