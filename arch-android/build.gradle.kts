plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.mavenPublish)
}

android {
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()
    namespace = "com.composure.arch.android"
    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexplicit-api=strict",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
        )
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":arch"))
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    if (project.hasProperty("signingInKey")) {
        signAllPublications()
    }

    pom {
        name.set("Composure Arch Android")
        description.set("Android ViewModel integration for Composure.")
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
