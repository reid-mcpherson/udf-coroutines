plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    explicitApi()

    android {
        namespace = "com.composure.arch"
        compileSdk {
            version =
                release(
                    libs.versions.compileSdk
                        .get()
                        .toInt(),
                )
        }
        minSdk {
            version =
                release(
                    libs.versions.minSdk
                        .get()
                        .toInt(),
                )
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
                }
            }
        }
    }

    jvm()

    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexplicit-api=strict",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }
        jvmTest.dependencies {
            implementation(libs.junit)
            implementation(libs.coroutines.test)
            implementation(libs.mockk)
            implementation(libs.truth)
            implementation(libs.turbine)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    if (project.hasProperty("signingInKey")) {
        signAllPublications()
    }

    pom {
        name.set("Composure Arch")
        description.set("Platform-agnostic UDF core with Kotlin Coroutines.")
        url.set("https://github.com/reid-mcpherson/udf-coroutines")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("reid-mcpherson")
                name.set("Reid McPherson")
            }
        }
        scm {
            url.set("https://github.com/reid-mcpherson/udf-coroutines")
            connection.set("scm:git:git://github.com/reid-mcpherson/udf-coroutines.git")
            developerConnection.set("scm:git:ssh://git@github.com/reid-mcpherson/udf-coroutines.git")
        }
    }
}
