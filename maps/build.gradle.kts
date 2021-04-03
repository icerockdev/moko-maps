/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinKapt)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines) {
        isForce = true
    }

    commonMainApi(Deps.Libs.MultiPlatform.mokoResources)
    commonMainApi(Deps.Libs.MultiPlatform.mokoGeo)
    commonMainApi(Deps.Libs.MultiPlatform.mokoGraphics)
    commonMainApi(Deps.Libs.MultiPlatform.mokoParcelize)

    commonMainImplementation(Deps.Libs.MultiPlatform.mokoPermissions.common)
}
