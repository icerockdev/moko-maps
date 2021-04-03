/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    plugin(Deps.Plugins.detekt) apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()

        jcenter {
            content {
                includeGroup("org.jetbrains.trove4j")
            }
        }
    }
    dependencies {
        plugin(Deps.Plugins.mokoResources)
        plugin(Deps.Plugins.kotlinSerialization)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()

        maven { url = uri("https://mapbox.bintray.com/mapbox") }

        jcenter {
            content {
                includeGroup("org.jetbrains.trove4j")
                includeGroup("org.jetbrains.kotlinx")
            }
        }
    }

    apply(plugin = Deps.Plugins.detekt.id)

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        input.setFrom(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin",
            "src/iosX64Main/kotlin"
        )
    }

    dependencies {
        "detektPlugins"(Deps.Libs.Detekt.detektFormatting)
    }

    plugins.withId(Deps.Plugins.androidLibrary.id) {
        configure<com.android.build.gradle.LibraryExtension> {
            compileSdkVersion(Deps.Android.compileSdk)

            defaultConfig {
                minSdkVersion(Deps.Android.minSdk)
                targetSdkVersion(Deps.Android.targetSdk)
            }
        }
    }

    plugins.withId(Deps.Plugins.mavenPublish.id) {
        group = "dev.icerock.moko"
        version = Deps.mokoMapsVersion

        val javadocJar by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        configure<PublishingExtension> {
            repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "OSSRH"

                credentials {
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_KEY")
                }
            }

            publications.withType<MavenPublication> {
                // Stub javadoc.jar artifact
                artifact(javadocJar.get())

                // Provide artifacts information requited by Maven Central
                pom {
                    name.set("MOKO maps")
                    description.set("Control your map from common code for mobile (android & ios) Kotlin Multiplatform development")
                    url.set("https://github.com/icerockdev/moko-maps")
                    licenses {
                        license {
                            url.set("https://github.com/icerockdev/moko-maps/blob/master/LICENSE.md")
                        }
                    }

                    developers {
                        developer {
                            id.set("Alex009")
                            name.set("Aleksey Mikhailov")
                            email.set("aleksey.mikhailov@icerockdev.com")
                        }
                        developer {
                            id.set("prokopishin")
                            name.set("Nikita Prokopishin")
                            email.set("nprokopishin@icerockdev.com")
                        }
                        developer {
                            id.set("Dorofeev")
                            name.set("Andrey Dorofeev")
                            email.set("adorofeev@icerockdev.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:ssh://github.com/icerockdev/moko-maps.git")
                        developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-maps.git")
                        url.set("https://github.com/icerockdev/moko-maps")
                    }
                }
            }

            apply(plugin = Deps.Plugins.signing.id)

            configure<SigningExtension> {
                val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
                val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
                val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                    String(Base64.getDecoder().decode(base64Key))
                }
                if (signingKeyId != null) {
                    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                    sign(publications)
                }
            }
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
