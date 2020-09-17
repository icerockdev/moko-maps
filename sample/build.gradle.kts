/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

subprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            listOf(
                Deps.Libs.MultiPlatform.mokoMaps to ":maps",
                Deps.Libs.MultiPlatform.mokoMapsGoogle to ":maps-google",
                Deps.Libs.MultiPlatform.mokoMapsMapbox to ":maps-mapbox"
            ).flatMap { (mpl, project) ->
                listOfNotNull(
                    mpl.common,
                    mpl.iosX64,
                    mpl.iosArm64
                ).map { it to project }
            }.forEach { (moduleSpec, projectSpec) ->
                substitute(module(moduleSpec))
                    .with(project(projectSpec))
            }
        }
    }
}
