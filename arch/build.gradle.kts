plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    `maven-publish`
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

// KMP creates publications eagerly — afterEvaluate is no longer needed.
// configureEach applies POM metadata to all auto-generated publications:
//   arch (metadata), arch-android, arch-iosarm64, arch-iossimulatorarm64, arch-jvm
publishing {
    publications.withType<MavenPublication>().configureEach {
        groupId = project.property("GROUP").toString()
        version = project.property("VERSION_NAME").toString()
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
        }
    }
}
